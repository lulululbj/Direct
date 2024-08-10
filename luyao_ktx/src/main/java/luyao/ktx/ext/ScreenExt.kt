package luyao.ktx.ext

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import android.util.TypedValue
import android.view.WindowManager

/**
 *  @author: luyao
 * @date: 2021/7/4 上午7:16
 */

fun Context.getScreenWidth(): Int {
    val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val point = Point()
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
            display?.getRealSize(point)
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 -> {
            wm.defaultDisplay.getRealSize(point)
        }
        else -> {
            wm.defaultDisplay.getSize(point)
        }
    }
    return point.x
}

fun Context.getScreenHeight(): Int {
    val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val point = Point()
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
            display?.getRealSize(point)
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 -> {
            wm.defaultDisplay.getRealSize(point)
        }
        else -> {
            wm.defaultDisplay.getSize(point)
        }
    }
    return point.y
}

fun Context.getAppScreenWidth(): Int {
    val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val point = Point()
    wm.defaultDisplay.getSize(point)
    return point.x
}

fun Context.getAppScreenHeight(): Int {
    val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val point = Point()
    wm.defaultDisplay.getSize(point)
    return point.y
}