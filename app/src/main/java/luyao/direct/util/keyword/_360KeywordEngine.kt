package luyao.direct.util.keyword

import org.json.JSONArray
import org.json.JSONObject

class _360KeywordEngine : KeywordEngine {

    override val searchUrl: String
        get() = "https://sug.so.360.cn/suggest?encodein=utf-8&encodeout=utf-8&format=json&word=%s&callback=direct"

    override fun parseResponse(content: String): List<String> {
        val result = mutableListOf<String>()
        try {
            val start = content.indexOfFirst { it == '(' }
            val end = content.lastIndexOf(')')
            if (start == -1 || end == -1 || start >= end) return result
            val jsonArray = JSONObject(content.substring(start + 1, end)).getJSONArray("result")
            for (i in 0 until jsonArray.length()) {
                result.add(jsonArray.getJSONObject(i).optString("word"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
}