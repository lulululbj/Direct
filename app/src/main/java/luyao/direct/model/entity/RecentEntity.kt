package luyao.direct.model.entity

import android.content.Intent
import android.os.Parcelable
import android.util.Base64
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import luyao.direct.DirectApp
import luyao.direct.R
import luyao.direct.model.dao.addAppCount
import luyao.direct.model.dao.addDirectCount
import luyao.ktx.ext.openApp
import luyao.ktx.ext.startScheme
import luyao.ktx.ext.toast

/**
 * author: luyao
 * date:   2021/9/6 10:08
 */
@Parcelize
@JsonClass(generateAdapter = true)
data class RecentEntity(
    val type: Int, // 0 AppEntity 1 DirectEntity
    val name: String,
    val packageName: String,
    val directId: Int, // 废弃
    val scheme: String,
    val lastTime: Long,
    val count: Int,
    var order: Int,
    val starTime: Long,
    val id: String = "" // 绑定的 id
) : Comparable<RecentEntity>, Parcelable {
    fun go() {
        if (type == 0) {
            DirectApp.App.openApp(packageName)
            addAppCount(packageName)
        } else {
            DirectApp.App.startScheme(scheme) {
                toast(R.string.scheme_unavaliable)
            }
            addDirectCount(id)
        }
    }

    fun getIntent(): Intent? {
        return if (type == 0) {
            DirectApp.App.packageManager.getLaunchIntentForPackage(packageName)
        } else {
            val intent =
                if (scheme.startsWith("android-app")) Intent.parseUri(
                    scheme,
                    Intent.URI_ANDROID_APP_SCHEME
                )
                else Intent.parseUri(scheme, Intent.URI_INTENT_SCHEME)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent
        }
    }

    override fun compareTo(other: RecentEntity): Int {
        return if (other.lastTime == this.lastTime)
            other.count - this.count
        else (other.lastTime - this.lastTime).toInt()
    }
}

fun AppEntity.toRecentEntity() =
    RecentEntity(
        0,
        appName,
        packageName,
        0,
        "",
        lastTime,
        count,
        if (starOrder == 0) Int.MAX_VALUE else starOrder,
        starTime,
        packageName,
    )

//fun DirectEntity.toRecentEntity() =
//    RecentEntity(
//        1,
//        label,
//        packageName,
//        id,
//        scheme,
//        lastTime,
//        count,
//        if (starOrder == 0) Int.MAX_VALUE else starOrder,
//        starTime,
//        id.toString()
//    )

fun NewDirectEntity.toRecentEntity() =
    RecentEntity(
        1,
        label,
        packageName,
        0,
        scheme,
        lastTime,
        count,
        if (starOrder == 0) Int.MAX_VALUE else starOrder,
        starTime,
        id
    )

