package luyao.direct.model.entity

/**
 * Description:
 * Author: luyao
 * Date: 2022/10/18 23:29
 */
data class AppDirect(
    val appName: String,
    val packageName: String,
    val label: String,
    val scheme: String,
    val className: String,
    var enabled: Boolean = false
)