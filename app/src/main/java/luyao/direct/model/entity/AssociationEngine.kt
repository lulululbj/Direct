package luyao.direct.model.entity

data class AssociationEngine(
    val id: Int,
    val name: String,
    val url: String
)

val associationEngineList = arrayListOf(
    AssociationEngine(0, "百度", "https://suggestion.baidu.com/su?wd=%s"),
    AssociationEngine(1, "必应", "https://sg1.api.bing.com/qsonhs.aspx?q=%s"),
    AssociationEngine(
        2,
        "淘宝",
        "https://suggest.taobao.com/sug?area=etao&code=utf-8&q=%s"
    ),
    AssociationEngine(
        3,
        "谷歌",
        "https://suggestqueries.google.com/complete/search?client=youtube&q=%s&jsonp=direct"
    ),
    AssociationEngine(
        4,
        "360 搜索",
        "https://sug.so.360.cn/suggest?encodein=utf-8&encodeout=utf-8&format=json&word=%s&callback=direct"
    )
)