package luyao.direct.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import luyao.direct.databinding.ItemRecentBinding
import luyao.direct.databinding.ItemSelectAppBinding
import luyao.direct.model.AppIconCache
import luyao.direct.model.entity.AppEntity
import luyao.direct.util.loadIcon

/**
 * Description:
 * Author: luyao
 * Date: 2022/8/3 13:21
 */
class ChooseGestureViewDelegate(
    val appClick: (AppEntity) -> Unit,
    val appLongClick: (AppEntity) -> Unit
) :
    ItemViewDelegate<AppEntity, ChooseGestureViewDelegate.AppViewHolder>() {

    private var isSelectMode = true
    private var selectListener: ((AppEntity, Boolean) -> Unit)? = null

    fun setSelectListener(listener: (AppEntity, Boolean) -> Unit) {
        selectListener = listener
    }

    override fun onBindViewHolder(holder: AppViewHolder, item: AppEntity) {
        holder.binding.run {
            recentName.text = item.appName
            recentName.checkScroll()
            item.loadIcon(recentIcon)
            root.setOnClickListener {
                    appClick(item)
            }
            root.setOnLongClickListener {
                if (!isSelectMode) {
                    appLongClick(item)
                }
                true
            }
        }
    }


    override fun onCreateViewHolder(context: Context, parent: ViewGroup): AppViewHolder {
        return AppViewHolder(ItemRecentBinding.inflate(LayoutInflater.from(context)))
    }

    fun setSelectMode(isSelectMode: Boolean) {
        this.isSelectMode = isSelectMode
        adapter.notifyItemRangeChanged(0, adapter.itemCount, "isSelect")
    }

    fun isSelectMode() = isSelectMode

    class AppViewHolder(val binding: ItemRecentBinding) :
        RecyclerView.ViewHolder(binding.root)
}