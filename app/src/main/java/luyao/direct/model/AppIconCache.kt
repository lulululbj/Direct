package luyao.direct.model

import android.content.pm.LauncherActivityInfo
import android.graphics.Bitmap
import android.util.DisplayMetrics
import android.util.LruCache
import android.widget.ImageView
import androidx.annotation.IntRange
import androidx.core.graphics.drawable.toBitmap
import luyao.direct.DirectApp
import luyao.direct.util.DirectInit
import luyao.ktx.ext.dp2px

/**
 * author: luyao
 * date:   2021/10/20 14:45
 */
object AppIconCache {

    private class AppIconLruCache(@IntRange(from = 1) maxSize: Int) :
        LruCache<String, Bitmap>(maxSize) {
        override fun sizeOf(key: String, value: Bitmap): Int {
            return value.byteCount / 1024
        }
    }

    private val lruCache: LruCache<String, Bitmap>

    init {
        val maxMemory = Runtime.getRuntime().maxMemory() / 1024
        val cacheSize = maxMemory / 4
        lruCache = AppIconLruCache(cacheSize.toInt())
    }

    fun put(packageName: String, bitmap: Bitmap) {
//        if (get(packageName) == null)
        lruCache.put(packageName, bitmap)
    }

    fun get(packageName: String): Bitmap? {
        return lruCache[packageName]
    }

    fun setImageViewBitmap(packageName: String, label: String, imageView: ImageView) {
        // 不能仅使用 packageName 来区分，同一个应用可能有多个快捷方式
        val cacheBitmap = get(packageName + label)
        if (cacheBitmap != null && !cacheBitmap.isRecycled) {
            imageView.setImageBitmap(cacheBitmap)
        } else {
            try {
                val viewWidth = if (imageView.width > 0) imageView.width else dp2px(36).toInt()

                val bitmap = DirectApp.App.packageManager.run {
                    getPackageInfo(packageName, 0).applicationInfo.loadIcon(this)
                        .toBitmap(width = viewWidth, height = viewWidth)
                }
                if (MMKVConstants.iconPack.isNotEmpty()) {
                    val icon =
                        DirectInit.iconPack?.getDrawableIconForPackage(packageName, null)
                            ?.toBitmap(width = viewWidth, height = viewWidth) ?: bitmap
                    imageView.setImageBitmap(icon)
                    put(packageName + label, icon)
                } else {
                    imageView.setImageBitmap(bitmap)
                    put(packageName + label, bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun checkCache(packageName: String, activityInfo: LauncherActivityInfo? = null) {
        if (get(packageName) == null) {

            val bitmap = activityInfo?.getBadgedIcon(DisplayMetrics.DENSITY_HIGH)
                ?.toBitmap(width = dp2px(36).toInt(), height = dp2px(36).toInt())
                ?: DirectApp.App.packageManager.run {
                    getPackageInfo(packageName, 0).applicationInfo.loadIcon(this)
                        .toBitmap(width = dp2px(36).toInt(), height = dp2px(36).toInt())
                }

            if (MMKVConstants.iconPack.isNotEmpty()) {
                val icon =
                    DirectInit.iconPackManager.IconPack().getIconForPackage(packageName, bitmap)
                put(packageName, icon)
            } else {
                put(packageName, bitmap)
            }
        }
    }
}