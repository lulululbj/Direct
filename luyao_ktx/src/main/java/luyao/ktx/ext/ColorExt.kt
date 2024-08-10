package luyao.ktx.ext

import android.graphics.Color

/**
 * author: luyao
 * date:   2021/10/28 17:18
 */

fun Int.toTextColor(darkColor: Int, lightColor: Int): Int {
    val level = Color.red(this) * 0.299 + Color.green(this) * 0.587 + Color.blue(this) * 0.114
    return if (level > 192) darkColor else lightColor
}