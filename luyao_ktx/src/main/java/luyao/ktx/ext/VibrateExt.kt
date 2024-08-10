package luyao.ktx.ext

import android.content.Context
import android.os.Vibrator

fun Context.vibrate(milliseconds: Long = 100) {
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    vibrator.vibrate(milliseconds)
}