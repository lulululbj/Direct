package luyao.ktx.net

import luyao.ktx.model.BizResult
import okhttp3.Request
import okio.Timeout
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Description:
 * Author: luyao
 * Date: 2022/12/19 15:43
 */
class SuspendBizResultCallAdapterFactory(
    private val failureHandler: FailureHandler? = null
) : CallAdapter.Factory() {

    /**
     * [onFailure] will be called when [BizResult.isFailure]
     */
    fun interface FailureHandler {
        fun onFailure(throwable: Throwable)
    }

    private var hasConverterForBizResult: Boolean? = null
    private fun Retrofit.hasConverterForBizResultType(resultType: Type): Boolean {
        return if (hasConverterForBizResult == true) true else kotlin.runCatching {
            nextResponseBodyConverter<BizResult<*>>(null, resultType, arrayOf())
        }.isSuccess.also { hasConverterForBizResult = true }
    }

    /**
     * Represents Type `Call<T>`, where `T` is passed in [dataType]
     */
    private class CallDataType(private val dataType: Type) : ParameterizedType {
        override fun getActualTypeArguments(): Array<Type> = arrayOf(dataType)
        override fun getRawType(): Type = Call::class.java
        override fun getOwnerType(): Type? = null
    }

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        // suspend function is represented by `Call<BizResult<T>`
        if (getRawType(returnType) != Call::class.java) return null
        if (returnType !is ParameterizedType) return null

        // check BizResult<T>
        val resultType: Type = getParameterUpperBound(0, returnType)
        if (getRawType(resultType) != BizResult::class.java || resultType !is ParameterizedType) return null

        val dataType = getParameterUpperBound(0, resultType)
        val delegateType =
            if (retrofit.hasConverterForBizResultType(resultType)) returnType else CallDataType(
                dataType
            )
        val delegate: CallAdapter<*, *> = retrofit.nextCallAdapter(this, delegateType, annotations)
        return CatchingCallAdapter(delegate, failureHandler)
    }

    private class CatchingCallAdapter(
        private val delegate: CallAdapter<*, *>,
        private val failureHandler: FailureHandler?
    ) : CallAdapter<Any, Call<BizResult<*>>> {
        override fun responseType(): Type = delegate.responseType()
        override fun adapt(call: Call<Any>): Call<BizResult<*>> = CatchingCall(call, failureHandler)
    }

    private class CatchingCall(
        private val delegate: Call<Any>,
        private val failureHandler: FailureHandler?
    ) : Call<BizResult<*>> {
        override fun enqueue(callback: Callback<BizResult<*>>) =
            delegate.enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        callback.onResponse(
                            this@CatchingCall,
                            Response.success(
                                BizResult.Success(body)
                            )
                        )
                    } else {
                        val throwable = HttpException(response)
                        failureHandler?.onFailure(throwable)
                        callback.onResponse(
                            this@CatchingCall,
                            Response.success(BizResult.Error(throwable))
                        )
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    failureHandler?.onFailure(t)
                    callback.onResponse(this@CatchingCall, Response.success(BizResult.Error(t)))
                }
            })

        override fun clone(): Call<BizResult<*>> = CatchingCall(delegate, failureHandler)

        override fun execute(): Response<BizResult<*>> {
            throw UnsupportedOperationException("Suspend function should not be blocking.")
        }

        override fun isExecuted(): Boolean = delegate.isExecuted
        override fun cancel() = delegate.cancel()
        override fun isCanceled(): Boolean = delegate.isCanceled
        override fun request(): Request = delegate.request()
        override fun timeout(): Timeout = delegate.timeout()
    }
}