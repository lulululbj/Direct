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
class EngineEditViewDelegate :
    ItemViewDelegate<NewDirectEntity, EngineEditViewDelegate.ToolViewHolder>() {

    //    private var itemTouchHelper: ItemTouchHelper? = null
    private var clickListener: ((NewDirectEntity, View, Int) -> Unit)? = null
//    private var longClickListener: ((View, DirectEntity, Int) -> Unit)? = null

//    fun setOnDragListener(touchHelper: ItemTouchHelper?) {
//        itemTouchHelper = touchHelper
//    }

    fun setClickListener(action: (NewDirectEntity, View, Int) -> Unit) {
        this.clickListener = action
    }

//    fun setLongClickListener(action: (View, DirectEntity, Int) -> Unit) {
//        this.longClickListener = action
//    }

    override fun onBindViewHolder(holder: ToolViewHolder, item: NewDirectEntity) {
        holder.binding.run {
            item.loadIcon(toolItem)
            if (item.showPanel == 0) {
                toolItem.alpha = 0.4f
            }
            toolRoot.setOnClickListener {
                clickListener?.invoke(
                    item,
                    holder.itemView,
                    holder.bindingAdapterPosition
                )
            }
//            toolRoot.setOnLongClickListener {
//                longClickListener?.invoke(it, item, holder.layoutPosition)
//                true
//            }
//            itemTouchHelper?.let {
//                toolRoot.setOnTouchListener { _, event ->
//                    if (itemTouchHelper != null && event.action == MotionEvent.ACTION_DOWN) {
//                        it.startDrag(holder)
//                    }
//                    false
//                }
//            }
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ToolViewHolder {
        val binding = ItemToolPopBinding.inflate(LayoutInflater.from(context))
        return ToolViewHolder(binding)
    }

    class ToolViewHolder(val binding: ItemToolPopBinding) : RecyclerView.ViewHolder(binding.root)


}