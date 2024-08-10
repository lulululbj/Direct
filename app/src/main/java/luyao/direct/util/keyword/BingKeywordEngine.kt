package luyao.direct.util.keyword

import org.json.JSONObject

class BingKeywordEngine : KeywordEngine {

    override val searchUrl: String
        get() = "https://sg1.api.bing.com/qsonhs.aspx?q=%s"

    override fun parseResponse(content: String): List<String> {
        val result = mutableListOf<String>()
        try {
            val obj = JSONObject(content).getJSONObject("AS").getJSONArray("Results")
            for (i in 0 until obj.length()) {
                val itemArray = obj.getJSONObject(i).getJSONArray("Suggests")
                for (j in 0 until itemArray.length()) {
                    val item = itemArray.getJSONObject(j).optString("Txt")
                    result.add(item)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
}