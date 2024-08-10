package luyao.sdk.extension

import android.os.Build

/**
 * author: luyao
 * date:   2021/10/27 16:26
 */

fun isBeforeQ() = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
fun isBeforeR() = Build.VERSION.SDK_INT < Build.VERSION_CODES.R
fun isBeforeM() = Build.VERSION.SDK_INT < Build.VERSION_CODES.M