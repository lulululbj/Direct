package luyao.direct.util.keyword

import luyao.direct.model.net.DirectRetrofitClient
import luyao.ktx.util.YLog
import okhttp3.Request
import java.util.*

interface KeywordEngine {

    val searchUrl: String

    fun parseResponse(content: String): List<String>

    fun queryKeyword(word: String, callback: (List<String>) -> Unit) {
        val url = String.format(Locale.getDefault(), searchUrl, word)
//        YLog.e("keyword: $url")
        val client = DirectRetrofitClient.client
        val request: Request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return@use
            val result = parseResponse(response.body?.string() ?: "")
//            YLog.e("keyword: $result")
            callback.invoke(result)
        }
    }
}