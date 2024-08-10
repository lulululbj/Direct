package luyao.direct.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import com.sackcentury.shinebuttonlib.ShineButton
import luyao.direct.DirectApp
import luyao.direct.R
import luyao.direct.ext.getFocusString
import luyao.direct.model.AppDatabase
import luyao.direct.model.MMKVConstants
import luyao.direct.model.dao.addAppCount
import luyao.direct.model.dao.saveSearchHistory
import luyao.direct.model.entity.AppEntity
import luyao.direct.model.entity.SearchHistoryEntity
import luyao.direct.ui.DirectActivity
import luyao.direct.util.loadIcon
import luyao.direct.view.getProperDirectDrawable
import luyao.ktx.app.AppScope
import luyao.ktx.ext.canLaunch
import luyao.ktx.ext.isAppEnabled
import luyao.ktx.ext.openApp
import luyao.ktx.ext.openAppSetting
import java.util.Locale

/**
 *  @author: luyao
 * @date: 2021/7/11 下午12:49
 */
class AppViewDelegate(
    val activity: DirectActivity,
    val shareApk: (AppEntity) -> Unit,
    val backupApk: (AppEntity) -> Unit
) :
    ItemViewDelegate<AppEntity, AppViewDelegate.ViewHolder>() {

    private var keyWord = ""
    private var clickIceListener: ((AppEntity) -> Unit)? = null

    fun setIceClickListener(listener: (AppEntity) -> Unit) {
        clickIceListener = listener
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_app, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: AppEntity) {
        holder.appRoot.setBackgroundResource(getProperDirectDrawable(item.isFirst, item.isLast))
//        holder.appRoot.background = cornerPanelBgDrawable
//        holder.appName.setTextColor(DirectApp.App.getProperTextColor())
        holder.appName.text = getFocusString(item.appName, keyWord)
        item.loadIcon(holder.appIcon)
        // 不可用，置灰显示
        if (!(DirectApp.App.isAppEnabled(item.packageName)) || !DirectApp.App.canLaunch(item.packageName)) {
            holder.appIcon.alpha = 0.3f
            // 被禁用，可能被冻结
            if (!DirectApp.App.isAppEnabled(item.packageName)) {
                holder.iceBt.setChecked(true, false)
                holder.iceBt.isEnabled = false
                holder.iceBt.visibility = View.VISIBLE
            }
        } else {
            holder.appIcon.alpha = 1f
            holder.iceBt.visibility = View.GONE
        }
        if (MMKVConstants.moreAppInfo) {
            holder.packageName.visibility = View.VISIBLE
            holder.packageName.text = getFocusString(item.packageName, keyWord)
            holder.versionName.visibility = View.VISIBLE
            holder.versionName.text =
                String.format(Locale.getDefault(), "%s/%s", item.versionName, item.versionCode)
        } else {
            holder.packageName.visibility = View.GONE
            holder.versionName.visibility = View.GONE
        }
        holder.appRoot.setOnClickListener {

            if (!DirectApp.App.openApp(item.packageName)) {
                if (DirectApp.App.isAppEnabled(item.packageName)) return@setOnClickListener
                // 被禁用，尝试解冻
                val schemes = arrayListOf(
                    "icebox://action/run_app/%s?user=0",
                    "intent:#Intent;component=me.zheteng.android.freezer/.support.LaunchAppActivity;S.me.zheteng.android.freezer.EXTRA_PACKAGE_NAME=%s;end",
                    "intent:#Intent;action=SHORT_CUT;launchFlags=0x4010000;component=me.yourbay.airfrozen/.main.uimodule.ShortcutHandleActivity;S.pkg=%s;end",
                    "intent:#Intent;action=SHORT_CUT;launchFlags=0x4010000;component=me.yourbay.airfrozen/.main.uimodule.ShortcutHandleActivity;S.pkg=%s;end",
                    "web1n.stopapp://action/run_app/%s?user=0"
                )
                for (scheme in schemes) {
                    val intent = Intent.parseUri(
                        String.format(
                            Locale.getDefault(),
                            scheme,
                            item.packageName
                        ), Intent.URI_INTENT_SCHEME
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    try {
                        DirectApp.App.startActivity(intent)
                        break
                    } catch (e: Exception) {
                        Log.e("ice", "launch : ${e.message}")
                        e.printStackTrace()
                    }

                }
            }
            addAppCount(item.packageName)
            saveSearchHistory(keyWord, SearchHistoryEntity.APP)
            if (MMKVConstants.autoClose) {
                activity.finish()
            }
        }

        holder.appRoot.setOnLongClickListener {
            showPopMenu(it, item)
//            val shortcut = ShortcutInfoCompat.Builder(DirectApp.App, "1")
//                .setShortLabel(item.appName)
//                .setIcon(IconCompat.createWithBitmap(AppIconCache.get(item.packageName)))
//                .setIntent(DirectApp.App.packageManager.getLaunchIntentForPackage(item.packageName)!!)
//                .build()
//            ShortcutManagerCompat.pushDynamicShortcut(DirectApp.App,shortcut)
            true
        }
        holder.startBt.run {
            setOnCheckStateChangeListener(null)
            setChecked(item.isStar == 1, false)
            setOnCheckStateChangeListener { _, checked ->
                item.isStar = if (checked) 1 else 0
                AppScope.launchIO {
                    AppDatabase.getInstance(DirectApp.App).appDao()
                        .updateStarStatus(item.packageName, item.isStar, System.currentTimeMillis())
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, item: AppEntity, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, item)
        } else {
            holder.appRoot.setBackgroundResource(getProperDirectDrawable(item.isFirst, item.isLast))
            holder.appName.text = getFocusString(item.appName, keyWord)
            // 不可用，置灰显示
            if (!(DirectApp.App.isAppEnabled(item.packageName)) || !DirectApp.App.canLaunch(item.packageName)) {
                holder.appIcon.alpha = 0.3f
                // 被禁用，可能被冻结
                if (!DirectApp.App.isAppEnabled(item.packageName)) {
                    holder.iceBt.setChecked(true, false)
                    holder.iceBt.isEnabled = false
                    holder.iceBt.visibility = View.VISIBLE
                }
            } else {
                holder.appIcon.alpha = 1f
                holder.iceBt.visibility = View.GONE
            }
            if (MMKVConstants.moreAppInfo) {
                holder.packageName.visibility = View.VISIBLE
                holder.packageName.text = getFocusString(item.packageName, keyWord)
                holder.versionName.visibility = View.VISIBLE
                holder.versionName.text =
                    String.format(Locale.getDefault(), "%s/%s", item.versionName, item.versionCode)
            } else {
                holder.packageName.visibility = View.GONE
                holder.versionName.visibility = View.GONE
            }
        }
    }

    fun setKeyWord(toString: String) {
        keyWord = toString
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appIcon: ImageView = itemView.findViewById(R.id.appIcon)
        val appName: TextView = itemView.findViewById(R.id.appName)
        val packageName: TextView = itemView.findViewById(R.id.packageName)
        val versionName: TextView = itemView.findViewById(R.id.versionName)
        val appRoot: ConstraintLayout = itemView.findViewById(R.id.appRoot)
        val startBt: ShineButton = itemView.findViewById(R.id.startBt)
        val iceBt: ShineButton = itemView.findViewById(R.id.iceBt)
    }

    private fun showPopMenu(view: View, appEntity: AppEntity) {
        PopupMenu(activity, view, Gravity.BOTTOM).run {
            menuInflater.inflate(R.menu.menu_app, menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.openAppSetting -> {
                        DirectApp.App.openAppSetting(appEntity.packageName)
                    }

                    R.id.backupApk -> {
                        backupApk.invoke(appEntity)
                    }

                    R.id.shareApk -> {
                        shareApk.invoke(appEntity)
                    }
                }
                true
            }
            show()
        }
    }


}