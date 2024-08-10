package luyao.ktx.net

import com.squareup.moshi.Moshi
import luyao.ktx.model.MoshiResultTypeAdapterFactory
import luyao.ktx.util.YLog
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

abstract class BaseRetrofitClient {

    companion object {
        const val TIME_OUT_SECONDS = 10L
    }

    val moshi: Moshi = Moshi.Builder()
        .add(MoshiResultTypeAdapterFactory())
        .build()

    val client: OkHttpClient
        get() {
            val builder = OkHttpClient.Builder()
            val logging = HttpLoggingInterceptor().apply {
                level =
//                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.BASIC
                    HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(logging)
                .connectTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
            handlerOkHttpClientBuilder(builder)
            return builder.build()
        }

    protected abstract fun handlerOkHttpClientBuilder(builder: OkHttpClient.Builder)

    inline fun <reified S> getService(baseUrl: String): S {
        return Retrofit.Builder()
            .client(client)
            .addCallAdapterFactory(SuspendBizResultCallAdapterFactory {
                YLog.e("suspend call error: ${it.message}")
            })
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(baseUrl)
            .build().create(S::class.java)
    }
}