package luyao.sdk.common

import java.text.SimpleDateFormat
import java.util.*

/**
 * author: luyao
 * date:   2021/10/15 14:18
 */

fun formatLongTime(time: Long): String {
    val format = SimpleDateFormat(" yyyy-MM-dd HH:mm:ss ", Locale.getDefault())
    return format.format(Date(time))
}