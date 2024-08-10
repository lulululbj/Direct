package luyao.ktx.model

import com.squareup.moshi.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Description:
 * Author: luyao
 * Date: 2022/12/19 14:59
 */
class MoshiResultTypeAdapterFactory : JsonAdapter.Factory {
    override fun create(
        type: Type,
        annotations: MutableSet<out Annotation>,
        moshi: Moshi
    ): JsonAdapter<*>? {
        val rawType = type.rawType
        if (rawType != kotlin.Result::class.java) return null

        val dataType: Type =
            (type as? ParameterizedType)?.actualTypeArguments?.firstOrNull() ?: return null

        val dataTypeAdapter = moshi.nextAdapter<Any>(this, dataType, annotations)
        return ResultTypeAdapter(dataTypeAdapter)
    }
}

class ResultTypeAdapter<T>(
    private val dataTypeAdapter: JsonAdapter<T>
) : JsonAdapter<T>() {

    override fun fromJson(reader: JsonReader): T? {
        reader.beginObject()

        var errorCode: Int? = null
        var errorMsg: String? = null
        var data: Any? = null

        while (reader.hasNext()) {
            when (reader.nextName()) {
                "errorCode" -> errorCode = reader.nextString().toIntOrNull()
                "errorMsg" -> errorMsg = reader.nextString()
                "data" -> data = dataTypeAdapter.fromJson(reader)
                    ?: throw java.lang.IllegalStateException("Response field [data] should not be null")
                else -> reader.skipValue()
            }
        }

        reader.endObject()

        if (errorCode == null) {
            throw JsonDataException("Response field [errorCode] not present")
        }

        return if (errorCode != 0) throw BizException(errorCode, errorMsg)
        else data as T
    }

    override fun toJson(writer: JsonWriter, value: T?) {

    }
}

class BizException(val code: Int, message: String?) : java.lang.RuntimeException(message)