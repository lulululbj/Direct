package luyao.direct.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import luyao.direct.databinding.ItemRecentBinding
import luyao.direct.model.entity.NewDirectEntity
import luyao.direct.util.loadIcon

/**
 *  手势配置中选择快捷方式
 *  @author: luyao
 * @date: 2021/8/27 下午11:43
 */
class DirectSelectDelegate : ItemViewDelegate<NewDirectEntity, DirectSelectDelegate.ViewHolder>() {

    private var itemClickListener: ((View, Int, NewDirectEntity) -> Unit)? = null

    fun setItemClickListener(itemClickListener: ((View, Int, NewDirectEntity) -> Unit)?) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        val binding = ItemRecentBinding.inflate(LayoutInflater.from(context))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: NewDirectEntity) {
        item.loadIcon(holder.binding.recentIcon)
//        holder.binding.recentName.setTextColor(DirectApp.App.getProperTextColor())
        holder.binding.recentRoot.setOnClickListener {
            itemClickListener?.invoke(it, holder.bindingAdapterPosition, item)
        }
        holder.binding.recentName.run {
            text = item.label
            checkScroll()
        }
    }

    class ViewHolder(val binding: ItemRecentBinding) : RecyclerView.ViewHolder(binding.root)
}