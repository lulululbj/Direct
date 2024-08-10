package luyao.ktx.ext

import android.os.Build

/**
 * author: luyao
 * date:   2021/10/27 16:26
 */


fun isAfterT() = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
fun isBeforeR() = Build.VERSION.SDK_INT < Build.VERSION_CODES.R
fun isBeforeQ() = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
fun isBeforeP() = Build.VERSION.SDK_INT < Build.VERSION_CODES.P
fun isBeforeM() = Build.VERSION.SDK_INT < Build.VERSION_CODES.M
fun isAfterM() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

fun isAfterO() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O