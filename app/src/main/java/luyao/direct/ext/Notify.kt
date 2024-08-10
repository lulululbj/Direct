package luyao.direct.ext

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import luyao.direct.R
import luyao.direct.model.MMKVConstants
import luyao.direct.model.entity.RecentEntity
import luyao.direct.ui.DirectActivity
import luyao.direct.util.MoshiUtil

/**
 * author: luyao
 * date:   2021/8/31 15:29
 */

const val CHANNEL_ID = "luyao.direct"
const val NOTIFICATION_ID = 1
const val STAR_NOTIFICATION_ID = 2
const val RECENT_NOTIFICATION_ID = 3
const val KEY_TEXT_REPLY = "key_text_reply"
const val REPLAY_ACTION = "luyao.direct.notification"

/*
 * 普通通知
 */
fun Context.createNotification() {
    NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID)
    createNotificationChannel()

    val pendingIntent = PendingIntent.getActivity(
        this,
        1,
        Intent(this, DirectActivity::class.java),
        FLAG_IMMUTABLE
    )

    val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(applicationInfo.icon)
        .setContentTitle(getString(R.string.app_name))
        .setContentText(getString(R.string.click_to_search))
        .setOngoing(true)
        .setAutoCancel(false)
        .setLargeIcon(BitmapFactory.decodeResource(resources, applicationInfo.icon))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)

    NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notificationBuilder.build())

}

/*
 * 星标通知
 * [tag] 0 star 1 recent
 */
fun Context.createDirectNotification(tag: Int) {
    NotificationManagerCompat.from(this)
        .cancel(if (tag == 0) STAR_NOTIFICATION_ID else RECENT_NOTIFICATION_ID)
    createNotificationChannel()

    val pendingIntent = PendingIntent.getActivity(
        this,
        0,
        Intent(this, DirectActivity::class.java),
        FLAG_IMMUTABLE
    )

    val starList: List<RecentEntity>? =
        MoshiUtil.recentEntityListAdapter.fromJson(if (tag == 0) MMKVConstants.starJson else MMKVConstants.recentJson)
    if (starList.isNullOrEmpty()) return

    val bigRemoteViews = getBigRemoteView(starList)
    val normalRemoteViews = getNormalRemoteView(starList)

    val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setOngoing(true)
        .setAutoCancel(false)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setCustomContentView(normalRemoteViews)
        .setCustomBigContentView(bigRemoteViews)
//        .setStyle(NotificationCompat.DecoratedCustomViewStyle())


    NotificationManagerCompat.from(this).notify(
        if (tag == 0) STAR_NOTIFICATION_ID else RECENT_NOTIFICATION_ID,
        notificationBuilder.build()
    )
}

private fun Context.getBigRemoteView(starList: List<RecentEntity>): RemoteViews {
    val remoteViews = RemoteViews(packageName, R.layout.notification_layout)

    var groupCount = 0
    var groupRemoteViews = RemoteViews(packageName, R.layout.notification_sub_layout)
    remoteViews.addView(R.id.notificationLayout, groupRemoteViews)
    for (recentEntity in starList) {
        if (groupCount < 6) { // 当前布局未满 6 个
            groupCount++
        } else { // 已满 6 个，再开一行
            groupCount = 0
            groupRemoteViews = RemoteViews(packageName, R.layout.notification_sub_layout)
            remoteViews.addView(R.id.notificationLayout, groupRemoteViews)
        }

        val itemRemoteViews = RemoteViews(packageName, R.layout.notificaion_item_layout)
        itemRemoteViews.setTextViewText(R.id.notificationItemName, recentEntity.name)
        itemRemoteViews.setImageViewBitmap(
            R.id.notificationItemImg,
            packageManager.getPackageInfo(recentEntity.packageName, 0).applicationInfo.loadIcon(
                packageManager
            ).toBitmap()
        )
        val pendingIntent =
            PendingIntent.getActivity(this, 0, recentEntity.getIntent(), FLAG_IMMUTABLE)
        itemRemoteViews.setOnClickPendingIntent(R.id.notificationItemRoot, pendingIntent)
        groupRemoteViews.addView(R.id.notificationSubLayout, itemRemoteViews)
    }
    return remoteViews
}

private fun Context.getNormalRemoteView(starList: List<RecentEntity>): RemoteViews {
    val groupRemoteViews = RemoteViews(packageName, R.layout.notification_sub_layout)
    starList.forEachIndexed { index, recentEntity ->
        if (index >= 6) return@forEachIndexed
        val itemRemoteViews = RemoteViews(packageName, R.layout.notificaion_item_layout)
        itemRemoteViews.setTextViewText(R.id.notificationItemName, recentEntity.name)
        itemRemoteViews.setImageViewBitmap(
            R.id.notificationItemImg,
            packageManager.getPackageInfo(recentEntity.packageName, 0).applicationInfo.loadIcon(
                packageManager
            ).toBitmap()
        )
        val pendingIntent =
            PendingIntent.getActivity(this, 0, recentEntity.getIntent(), FLAG_IMMUTABLE)
        itemRemoteViews.setOnClickPendingIntent(R.id.notificationItemRoot, pendingIntent)
        groupRemoteViews.addView(R.id.notificationSubLayout, itemRemoteViews)
    }
    return groupRemoteViews
}

private fun Context.createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
            enableVibration(false)
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun Context.cancelNotification(id: Int) {
    val notificationManager: NotificationManager =
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.cancel(id)
}

fun main() {
    val a = 5
    val result = a / 6 + (if (a % 6 == 0) 0 else 1)
    println("$result")
}
