package luyao.direct.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import luyao.direct.DirectApp
import luyao.direct.R
import luyao.direct.model.MMKVConstants
import luyao.ktx.ext.dp2px

/**
 * author: luyao
 * date:   2021/11/4 10:53
 */
val cornerSize = dp2px(10)
val mainBgDrawable: Drawable = GradientDrawable().apply {
    shape = GradientDrawable.RECTANGLE
    cornerRadii = floatArrayOf(
        cornerSize,
        cornerSize,
        cornerSize,
        cornerSize,
        cornerSize,
        cornerSize,
        cornerSize,
        cornerSize
    )
    setColor(MMKVConstants.mainBgColor)
}

fun getProperPanelDrawable(isFirst: Boolean, isLast: Boolean) = when {
    isFirst and isLast -> cornerPanelBgDrawable()
    isFirst -> bottomCornerPanelBgDrawable()
    isLast -> topCornerPanelBgDrawable()
    else -> noCornerPanelBgDrawable()
}

fun getProperDirectDrawable(isFirst: Boolean, isLast: Boolean) = when {
    isFirst and isLast -> R.drawable.bg_single_direct
    isFirst -> R.drawable.bg_bottom_direct
    isLast -> R.drawable.bg_top_direct
    else -> R.drawable.bg_middle_direct
}

fun noCornerPanelBgDrawable(): Drawable = StateListDrawable().apply {
    val normalDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
//        setColor(MMKVConstants.panelBgColor)
        setColor(ContextCompat.getColor(DirectApp.App, R.color.white))
    }
    val pressedDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
//        val originColor = MMKVConstants.panelBgColor
        val originColor = ContextCompat.getColor(DirectApp.App, R.color.white)
        val color = Color.argb(
            0xAA,
            Color.red(originColor),
            Color.green(originColor),
            Color.blue(originColor)
        )
        setColor(color)
    }

    addState(intArrayOf(android.R.attr.state_pressed), pressedDrawable)
    addState(intArrayOf(), normalDrawable)
}

fun topCornerPanelBgDrawable(): Drawable = StateListDrawable().apply {
    val normalDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadii = floatArrayOf(cornerSize, cornerSize, cornerSize, cornerSize, 0f, 0f, 0f, 0f)
//        setColor(MMKVConstants.panelBgColor)
        setColor(ContextCompat.getColor(DirectApp.App, R.color.white))
    }
    val pressedDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadii = floatArrayOf(cornerSize, cornerSize, cornerSize, cornerSize, 0f, 0f, 0f, 0f)
//        val originColor = MMKVConstants.panelBgColor
        val originColor = ContextCompat.getColor(DirectApp.App, R.color.white)
        val color = Color.argb(
            0xAA,
            Color.red(originColor),
            Color.green(originColor),
            Color.blue(originColor)
        )
        setColor(color)
    }

    addState(intArrayOf(android.R.attr.state_pressed), pressedDrawable)
    addState(intArrayOf(), normalDrawable)
}

fun bottomCornerPanelBgDrawable(): Drawable = StateListDrawable().apply {
    val normalDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadii =
            floatArrayOf(0f, 0f, 0f, 0f, cornerSize, cornerSize, cornerSize, cornerSize, cornerSize)
//        setColor(MMKVConstants.panelBgColor)
        setColor(ContextCompat.getColor(DirectApp.App, R.color.white))
    }
    val pressedDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadii =
            floatArrayOf(0f, 0f, 0f, 0f, cornerSize, cornerSize, cornerSize, cornerSize, cornerSize)
//        val originColor = MMKVConstants.panelBgColor
        val originColor = ContextCompat.getColor(DirectApp.App, R.color.white)
        val color = Color.argb(
            0xAA,
            Color.red(originColor),
            Color.green(originColor),
            Color.blue(originColor)
        )
        setColor(color)
    }

    addState(intArrayOf(android.R.attr.state_pressed), pressedDrawable)
    addState(intArrayOf(), normalDrawable)
}

fun cornerPanelBgDrawable(): Drawable = StateListDrawable().apply {
    val normalDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadii = floatArrayOf(
            cornerSize,
            cornerSize,
            cornerSize,
            cornerSize,
            cornerSize,
            cornerSize,
            cornerSize,
            cornerSize
        )
//        setColor(MMKVConstants.panelBgColor)
        setColor(ContextCompat.getColor(DirectApp.App, R.color.white))
    }
    val pressedDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadii = floatArrayOf(
            cornerSize,
            cornerSize,
            cornerSize,
            cornerSize,
            cornerSize,
            cornerSize,
            cornerSize,
            cornerSize
        )
//        val originColor = MMKVConstants.panelBgColor
        val originColor = ContextCompat.getColor(DirectApp.App, R.color.white)
        val color = Color.argb(
            0xAA,
            Color.red(originColor),
            Color.green(originColor),
            Color.blue(originColor)
        )
        setColor(color)
    }

    addState(intArrayOf(android.R.attr.state_pressed), pressedDrawable)
    addState(intArrayOf(), normalDrawable)
}

fun Context.getPanelDrawable(index: Int): Drawable? {
    return AppCompatResources.getDrawable(
        applicationContext,
        if (index == 0) R.drawable.ic_browser else if (index == 1) R.drawable.ic_setting_shortcut else if (index == 2) R.drawable.ic_settings_star else R.drawable.ic_setting_search
    )
}