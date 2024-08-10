package luyao.direct.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import luyao.direct.databinding.ItemDirectCountBinding
import luyao.direct.model.entity.DirectCount
import luyao.direct.util.loadIcon

/**
 *  @author: luyao
 * @date: 2021/8/27 下午11:43
 */
class DirectCountViewDelegate :
    ItemViewDelegate<DirectCount, DirectCountViewDelegate.ViewHolder>() {

    private var itemClickListener: ((View, Int, DirectCount) -> Unit)? = null

    fun setItemClickListener(itemClickListener: ((View, Int, DirectCount) -> Unit)?) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        val binding = ItemDirectCountBinding.inflate(LayoutInflater.from(context))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: DirectCount) {
        if (item.count > 99) {
            holder.binding.directCount.text = "99"
        } else {
            holder.binding.directCount.text = item.count.toString()
        }
        item.loadIcon(holder.binding.directIcon)
        holder.binding.recentRoot.setOnClickListener {
            itemClickListener?.invoke(it, holder.bindingAdapterPosition, item)
        }
    }

    class ViewHolder(val binding: ItemDirectCountBinding) : RecyclerView.ViewHolder(binding.root)
}