package luyao.direct.util.keyword

import org.json.JSONObject

class TaobaoKeywordEngine : KeywordEngine {

    override val searchUrl: String
        get() = "https://suggest.taobao.com/sug?area=etao&code=utf-8&q=%s"

    override fun parseResponse(content: String): List<String> {
        val result = mutableListOf<String>()
        try {
            val array = JSONObject(content).getJSONArray("result")
            for (i in 0 until array.length()) {
                result.add(array.getJSONArray(i).optString(0))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
}