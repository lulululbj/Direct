package luyao.ktx.util

import android.text.format.DateFormat


/**
 * Description:
 * Author: luyao
 * Date: 2022/4/27 09:36
 */
const val FORMAT_yyyy_MM_dd_HH_mm = "yyyy-MM-dd HH:mm"

fun Long.toFormatString(format: String = "yyyy-MM-dd"): String {
    return DateFormat.format(format, this).toString()
}