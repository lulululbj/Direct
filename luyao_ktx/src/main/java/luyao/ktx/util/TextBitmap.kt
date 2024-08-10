package luyao.ktx.util

import android.graphics.*
import luyao.ktx.ext.dp2px


/**
 * author: luyao
 * date:   2021/11/1 20:08
 */
val colors = arrayOf(
    "#568AAD",
    "#17c295",
    "#4DA9EB",
    "#F2725E",
    "#B38979",
    "#568AAD"
)

fun getCircleTextBitmap(text: String, width: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG) // 抗锯齿
    val canvas = Canvas(bitmap)
    paint.color = Color.parseColor(colors[text.hashCode() % colors.size])
    paint.style = Paint.Style.FILL
    canvas.drawCircle(width / 2f, width / 2f, width / 2f, paint)
    paint.color = Color.WHITE
    paint.textSize = dp2px(20)
    val bounds = Rect()
    paint.getTextBounds(text, 0, text.length, bounds)
    val x: Float = (width - bounds.width() - bounds.left) / 2f
    val y: Float = (width + bounds.height() - bounds.bottom) / 2f
    canvas.drawText(text, x, y, paint)
    return bitmap
}

fun main() {
    for (i in 1..100) {
        println("${i % 6}")
    }
}