package luyao.direct.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import luyao.direct.databinding.ItemToolPopBinding
import luyao.direct.model.entity.NewDirectEntity
import luyao.direct.util.loadIcon

/**
 * author: luyao
 * date:   2021/10/8 15:03
 */
class EngineViewDelegate(
    private val engineClick: ((NewDirectEntity, View, Int) -> Unit)? = null,
    private val engineLongClick: ((NewDirectEntity) -> Unit)? = null
) : ItemViewDelegate<NewDirectEntity, EngineViewDelegate.ToolViewHolder>() {

    var isEditMode = false
    override fun onBindViewHolder(holder: ToolViewHolder, item: NewDirectEntity) {
        holder.binding.run {
            item.loadIcon(toolItem)
            toolRoot.setOnClickListener {
                engineClick?.invoke(
                    item,
                    holder.itemView,
                    holder.bindingAdapterPosition
                )
            }
            toolRoot.setOnLongClickListener {
                engineLongClick?.invoke(item)
                true
            }
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ToolViewHolder {
        val binding = ItemToolPopBinding.inflate(LayoutInflater.from(context))
        return ToolViewHolder(binding)
    }

    class ToolViewHolder(val binding: ItemToolPopBinding) : RecyclerView.ViewHolder(binding.root)
}