package luyao.direct.service

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.graphics.drawable.toBitmap
import com.tencent.mmkv.MMKV
import luyao.direct.R
import luyao.direct.model.MMKVConstants
import luyao.direct.model.entity.RecentEntity
import luyao.direct.util.MoshiUtil

/**
 *  @author: luyao
 * @date: 2021/9/14 下午11:42
 */
open class DirectWidgetProvider : AppWidgetProvider() {

    open val tag = 0

    companion object {
        const val WIDGET_ACTION = "luyao.direct.widget.action"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            val intent = Intent(
                context,
                if (tag == 0) StarWidgetRemoteViewsService::class.java else RecentWidgetRemoteViewsService::class.java
            )
            intent.putExtra("tag", tag)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
            val remoteViews = RemoteViews(context.packageName, R.layout.widget_layout)
            remoteViews.setRemoteAdapter(R.id.widgetGridView, intent)

            val itemIntent = Intent(
                context,
                if (tag == 0) StarWidgetProvider::class.java else RecentWidgetProvider::class.java
            )
            itemIntent.action = WIDGET_ACTION
            itemIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            itemIntent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                itemIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
            remoteViews.setPendingIntentTemplate(R.id.widgetGridView, pendingIntent)
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent?) {
        intent?.let {
            if (it.action == WIDGET_ACTION) {
                val json = it.getStringExtra("widgetItem")
                val recentEntity = json?.let { it1 -> MoshiUtil.recentEntityAdapter.fromJson(it1) }
                recentEntity?.go()
            }
        }
        super.onReceive(context, intent)
    }
}

class StarWidgetRemoteViewsService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return DirectWidgetRemoteViewsFactory(applicationContext, intent)
    }
}

class RecentWidgetRemoteViewsService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return DirectWidgetRemoteViewsFactory(applicationContext, intent)
    }
}

class DirectWidgetRemoteViewsFactory(private val context: Context, private val intent: Intent) :
    RemoteViewsService.RemoteViewsFactory {

    private var tag = 0

    private lateinit var recentEntityList: List<RecentEntity>

    override fun onCreate() {
        tag = intent.getIntExtra("tag", -1)
        MMKV.initialize(context)
        // starJson 和 recentJson 为 "" 时，要特殊处理，否则 Gson 会空指针
        val json = if (tag == 0) MMKVConstants.starJson else MMKVConstants.recentJson
        recentEntityList = if (json.isEmpty()) {
            arrayListOf()
        } else {
            MoshiUtil.recentEntityListAdapter.fromJson(json) ?: arrayListOf()
        }
    }

    override fun onDataSetChanged() {
        val json = if (tag == 0) MMKVConstants.starJson else MMKVConstants.recentJson
        recentEntityList = if (json.isEmpty()) {
            arrayListOf()
        } else {
            MoshiUtil.recentEntityListAdapter.fromJson(json) ?: arrayListOf()
        }
    }

    override fun onDestroy() {}

    override fun getCount() = recentEntityList.size

    override fun getViewAt(position: Int): RemoteViews {
        return constructRemoteViews(context, recentEntityList[position])
    }

    override fun getLoadingView() = null

    override fun getViewTypeCount() = 1

    override fun getItemId(position: Int) = position.toLong()

    override fun hasStableIds() = true

    private fun constructRemoteViews(
        context: Context,
        recentEntity: RecentEntity
    ): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_item_layout)
        remoteViews.setImageViewBitmap(
            R.id.recentIcon,
            context.packageManager.getPackageInfo(
                recentEntity.packageName,
                0
            ).applicationInfo.loadIcon(context.packageManager).toBitmap()
        )
        remoteViews.setTextViewText(R.id.recentName, recentEntity.name)

        remoteViews.setOnClickFillInIntent(
            R.id.recentRoot,
            Intent().apply {
                putExtra("widgetItem", MoshiUtil.recentEntityAdapter.toJson(recentEntity))
            }
        )
        return remoteViews
    }

}
