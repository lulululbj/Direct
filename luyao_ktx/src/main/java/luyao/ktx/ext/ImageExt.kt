package luyao.ktx.ext

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.InputStream
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.File


/**
 * author: luyao
 * date:   2021/10/29 14:47
 */

/**
 * Return bitmap.
 *
 * @param maxWidth  The maximum width.
 * @param maxHeight The maximum height.
 * @return bitmap
 */
fun InputStream.getBitmap(maxWidth: Int, maxHeight: Int): Bitmap? {
    val byteArray = this.readBytes()
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
    options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
    options.inJustDecodeBounds = false
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
}

fun calculateInSampleSize(
    options: BitmapFactory.Options,
    maxWidth: Int,
    maxHeight: Int
): Int {
    var height = options.outHeight
    var width = options.outWidth
    var inSampleSize = 1
    while (height > maxHeight || width > maxWidth) {
        height = height shr 1
        width = width shr 1
        inSampleSize = inSampleSize shl 1
    }
    return inSampleSize
}

fun Bitmap.scale(newWidth: Int, newHeight: Int): Bitmap {
    return scale(this, newWidth, newHeight, false)
}

fun scale(
    src: Bitmap,
    newWidth: Int,
    newHeight: Int,
    recycle: Boolean
): Bitmap {
    val ret = Bitmap.createScaledBitmap(src, newWidth, newHeight, true)
    if (recycle && !src.isRecycled && ret != src) src.recycle()
    return ret
}

fun Bitmap.toByteArray(
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
    quality: Int = 100
): ByteArray {
    val bos = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 100, bos)
    return bos.toByteArray()
}

fun ByteArray.toBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, size)
}

fun Uri.toBitmap(context: Context): Bitmap? {
    context.contentResolver.openFileDescriptor(this, "r").use {
        return BitmapFactory.decodeFileDescriptor(it?.fileDescriptor)
    }
}

fun Bitmap.saveToAlbum() {

}

