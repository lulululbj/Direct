package luyao.direct.util.keyword

import org.json.JSONArray

class GoogleKeywordEngine : KeywordEngine {

    override val searchUrl: String
        get() = "https://suggestqueries.google.com/complete/search?client=youtube&q=%s&jsonp=direct"

    override fun parseResponse(content: String): List<String> {
        val result = mutableListOf<String>()
        try {
            val start = content.indexOfFirst { it == '(' }
            val end = content.lastIndexOf(')')
            if (start == -1 || end == -1 || start >= end) return result
            val jsonArray = JSONArray(content.substring(start + 1, end)).getJSONArray(1)
            for (i in 0 until jsonArray.length()) {
                result.add(jsonArray.getJSONArray(i).optString(0))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
}