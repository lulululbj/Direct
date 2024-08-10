package luyao.direct.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import luyao.direct.DirectApp
import luyao.direct.R
import luyao.direct.databinding.ItemShowDirectBinding
import luyao.direct.ext.createShortcut
import luyao.direct.ext.go
import luyao.direct.model.entity.NewDirectEntity
import luyao.direct.util.loadIcon
import luyao.ktx.ext.openAppSetting
import luyao.ktx.ext.showConfirmDialog

/**
 * Description:
 * Author: luyao
 * Date: 2022/10/18 13:30
 */
class AppDirectViewDelegate(
    private val activity: Activity,
    private val appDirectListener: AppDirectListener? = null
) :
    ItemViewDelegate<NewDirectEntity, AppDirectViewDelegate.ViewHolder>() {

    class ViewHolder(val binding: ItemShowDirectBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ViewHolder, item: NewDirectEntity) {
        holder.binding.run {
            item.loadIcon(directIcon)
            directName.text = item.label
//            directEdit.isVisible = item.enabled == 0
//            directClass.isVisible = item.enabled == 1
//            directClass.text = item.scheme
            directRoot.setOnClickListener {
                directCheck.isChecked = !directCheck.isChecked
            }

            directEditMenu.setOnClickListener {
                PopupMenu(activity, it).run {
                    menuInflater.inflate(R.menu.menu_direct_edit, menu)
                    setOnMenuItemClickListener { menu ->
                        when (menu.itemId) {
                            R.id.edit_direct -> {
                                appDirectListener?.onClick(item)
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
            }

            directCheck.setOnCheckedChangeListener(null)
            directCheck.isChecked = item.enabled == 0
            directCheck.setOnCheckedChangeListener { _, isChecked ->
                item.enabled = if (isChecked) 0 else 1
                appDirectListener?.onCheckChanged(item, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup) =
        ViewHolder(ItemShowDirectBinding.inflate(LayoutInflater.from(context), parent, false))
}

interface AppDirectListener {
    fun onCheckChanged(direct: NewDirectEntity, newValue: Boolean)
    fun onClick(direct: NewDirectEntity)
}