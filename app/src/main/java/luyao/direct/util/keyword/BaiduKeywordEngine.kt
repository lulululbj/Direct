package luyao.direct.util.keyword

import luyao.direct.model.net.DirectRetrofitClient
import okhttp3.Request
import org.json.JSONObject
import java.util.*

class BaiduKeywordEngine : KeywordEngine {

    override val searchUrl: String
        get() = "https://suggestion.baidu.com/su?wd=%s"


    // direct({q:"设计",p:false,s:["设计logo","设计师怎么学","设计师设计一套房子多少钱","设计房子的软件","设计图制作软件","设计专业大学世界排名","设计师证怎么考","设计公司","设计软件","设计网站"]});
    override fun parseResponse(content: String): List<String> {
        val result = mutableListOf<String>()
        try {
            val start = content.indexOfFirst { it == '{' }
            val end = content.lastIndexOf('}')
            if (start == -1 || end == -1 || start >= end) return result
            val obj = JSONObject(content.substring(start, end + 1))
            val jsonArray = obj.getJSONArray("s")
            for (i in 0 until jsonArray.length()) {
                result.add(jsonArray[i] as String)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
}
