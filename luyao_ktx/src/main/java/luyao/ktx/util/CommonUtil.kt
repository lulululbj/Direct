package luyao.ktx.util

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.annotation.AnyRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import java.lang.reflect.Proxy

/**
 * Description:
 * Author: luyao
 * Date: 2022/12/14 15:57
 */

inline fun <reified T : Any> noOpDelegate(): T {
    val clazz = T::class.java
    return Proxy.newProxyInstance(clazz.classLoader, arrayOf(clazz)) { _, _, _ -> } as T
}

/**
 * 高亮关键字
 */
fun getHighlightString(
    originString: String,
    keyword: String,
    @ColorInt color: Int = Color.parseColor("#E91E63")
): SpannableStringBuilder {
    val start = originString.indexOf(keyword)
    val focusString = SpannableStringBuilder(originString)
    if (start < 0) return focusString
    focusString.setSpan(
        ForegroundColorSpan(color), start, start + keyword.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return focusString
}