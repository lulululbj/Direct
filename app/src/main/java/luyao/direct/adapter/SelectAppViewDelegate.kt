package luyao.direct.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import luyao.direct.databinding.ItemSelectAppBinding
import luyao.direct.model.entity.AppEntity
import luyao.direct.util.loadIcon

/**
 * Description:
 * Author: luyao
 * Date: 2022/8/3 13:21
 */
class SelectAppViewDelegate(
    val appClick: (AppEntity) -> Unit,
    val appLongClick: (AppEntity) -> Unit
) :
    ItemViewDelegate<AppEntity, SelectAppViewDelegate.AppViewHolder>() {

    private var isSelectMode = true
    private var selectListener: ((AppEntity, Boolean) -> Unit)? = null

    fun setSelectListener(listener: (AppEntity, Boolean) -> Unit) {
        selectListener = listener
    }

    override fun onBindViewHolder(holder: AppViewHolder, item: AppEntity) {
        holder.binding.run {
//            appCheck.isVisible = isSelectMode
//            if (isSelectMode) {
//                appCheck.setOnCheckedChangeListener(null)
//                appCheck.isChecked = item.isSelected
//            }
            appName.text = item.appName
            item.loadIcon(appIcon)
//            Glide.with(appIcon).load(item.appIcon).into(appIcon)
            root.setOnClickListener {
//                if (isSelectMode) {
//                    appCheck.isChecked = !appCheck.isChecked
//                } else {
                appClick(item)
//                }
            }
            root.setOnLongClickListener {
                if (!isSelectMode) {
                    appLongClick(item)
                }
                true
            }
//            appCheck.setOnCheckedChangeListener { _, isChecked ->
//                selectListener?.invoke(item, isChecked)
//            }
        }
    }

    override fun onBindViewHolder(holder: AppViewHolder, item: AppEntity, payloads: List<Any>) {
        if (payloads.isNotEmpty()) {
            holder.binding.run {
//                appCheck.isVisible = isSelectMode
//                appCheck.isChecked = item.isSelected
            }
        } else {
            onBindViewHolder(holder, item)
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): AppViewHolder {
        return AppViewHolder(ItemSelectAppBinding.inflate(LayoutInflater.from(context)))
    }

    fun setSelectMode(isSelectMode: Boolean) {
        this.isSelectMode = isSelectMode
        adapter.notifyItemRangeChanged(0, adapter.itemCount, "isSelect")
    }

    fun isSelectMode() = isSelectMode

    class AppViewHolder(val binding: ItemSelectAppBinding) :
        RecyclerView.ViewHolder(binding.root)
}