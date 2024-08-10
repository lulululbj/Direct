package luyao.ktx.ext

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

/**
 * Description:
 * Author: luyao
 * Date: 2023/9/6 17:19
 */

fun View.background(
    @ColorInt fillColor: Int = Color.WHITE,
    radius: Int = 0,
    @ColorInt strokeColor: Int = Color.WHITE,
    strokeWidth: Int = 0,
    shape: Int = GradientDrawable.RECTANGLE
) {
    background = GradientDrawable().apply {
        this.shape = shape
        setColor(fillColor)
        cornerRadius = dp2px(radius)
        setStroke(dp2px(strokeWidth).toInt(), strokeColor)
    }
}

fun View.background(
    @ColorInt fillColor: Int = Color.WHITE,
    topLeftRadius: Int = 0,
    topRightRadius: Int = 0,
    bottomLeftRadius: Int = 0,
    bottomRightRadius: Int = 0,
    @ColorInt strokeColor: Int = Color.WHITE,
    strokeWidth: Int = 0,
    shape: Int = GradientDrawable.RECTANGLE
) {
    background = GradientDrawable().apply {
        this.shape = shape
        setColor(fillColor)
        val tl = dp2px(topLeftRadius)
        val tr = dp2px(topRightRadius)
        val bl = dp2px(bottomLeftRadius)
        val br = dp2px(bottomRightRadius)
        cornerRadii = floatArrayOf(tl, tl, tr, tr, br, br, bl, bl)
        setStroke(dp2px(strokeWidth).toInt(), strokeColor)
    }
}