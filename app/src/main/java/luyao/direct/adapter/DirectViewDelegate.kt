package luyao.direct.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import luyao.direct.DirectApp
import luyao.direct.R
import luyao.direct.databinding.ItemAppBinding
import luyao.direct.ext.createShortcut
import luyao.direct.ext.getFocusString
import luyao.direct.ext.go
import luyao.direct.model.AppDatabase
import luyao.direct.model.MMKVConstants
import luyao.direct.model.dao.addDirectCount
import luyao.direct.model.dao.saveSearchHistory
import luyao.direct.model.entity.NewDirectEntity
import luyao.direct.model.entity.SearchHistoryEntity
import luyao.direct.ui.DirectActivity
import luyao.direct.ui.settings.direct.AppDirectListActivity
import luyao.direct.util.loadIcon
import luyao.direct.view.DirectEditDialog
import luyao.direct.view.getProperDirectDrawable
import luyao.ktx.app.AppScope
import luyao.ktx.ext.hideKeyboard
import luyao.ktx.ext.isAppInstalled
import luyao.ktx.ext.openAppSetting
import luyao.ktx.ext.showConfirmDialog
import luyao.ktx.ext.startActivity

/**
 *  @author: luyao
 * @date: 2021/9/25 上午6:44
 */
class DirectViewDelegate(val activity: DirectActivity) :
    ItemViewDelegate<NewDirectEntity, DirectViewDelegate.ViewHolder>() {

    private var keyWord = ""

    fun setKeyWord(toString: String) {
        keyWord = toString
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        return ViewHolder(ItemAppBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: NewDirectEntity) {
        holder.binding.run {
            item.loadIcon(appIcon)
            appRoot.setBackgroundResource(getProperDirectDrawable(item.isFirst, item.isLast))
//        holder.appName.setTextColor(DirectApp.App.getProperTextColor())
            appName.text = getFocusString(item.label, keyWord)
            appRoot.setOnClickListener {
                DirectApp.App.hideKeyboard(it)
                item.go(DirectApp.App)
                addDirectCount(item.id)
                saveSearchHistory(keyWord, SearchHistoryEntity.DIRECT)
                if (MMKVConstants.autoClose) {
                    activity.finish()
                }
            }
            startBt.run {
                setOnCheckStateChangeListener(null)
                setChecked(item.isStar == 1, false)
                setOnCheckStateChangeListener { _, checked ->
                    AppScope.launchIO {
                        item.isStar = if (checked) 1 else 0
                        AppDatabase.getInstance(DirectApp.App).newDirectDao()
                            .updateStarStatus(item.id, item.isStar, System.currentTimeMillis())
                    }
                }
            }

            appRoot.setOnLongClickListener {
                showPopMenu(appName, item)
                true
            }
        }
    }

    private fun showPopMenu(view: View, item: NewDirectEntity) {

        PopupMenu(activity, view).run {
            menuInflater.inflate(R.menu.menu_direct_edit, menu)
            setOnMenuItemClickListener { menu ->
                when (menu.itemId) {
                    R.id.edit_direct -> {
                        val directEditDialog  = DirectEditDialog(activity) {

                        }
                        directEditDialog.setDirect(item)
                        directEditDialog.show()
                    }
                    R.id.test_direct -> {
                        item.go(DirectApp.App)
                    }
                    R.id.add_direct_launcher -> {
                        activity.showConfirmDialog(message = activity.getString(R.string.check_shortcut_permission),
                            confirmText = R.string.has_authorization,
                            cancelText = R.string.go_to_setting,
                            onCancel = {
                                DirectApp.App.openAppSetting(DirectApp.App.packageName)
                            },
                            onConfirm = {
                                item.createShortcut()
                            })

                    }
                }
                true
            }
            show()
        }
//        PopupMenu(activity, view).apply {
//            menuInflater.inflate(R.menu.menu_direct, menu)
//            setOnMenuItemClickListener {
//                when (it.itemId) {
//                    R.id.menu_direct_see -> {
//                        activity.startActivity<AppDirectListActivity>(
//                            "packageName" to directEntity.packageName,
//                            "showAll" to false
//                        )
//                    }
//                    R.id.menu_direct_shortcuts -> {
//                        directEntity.createShortcut()
//                    }
//                }
//                true
//            }
//            show()
//        }
    }

    class ViewHolder(val binding: ItemAppBinding) : RecyclerView.ViewHolder(binding.root)

}