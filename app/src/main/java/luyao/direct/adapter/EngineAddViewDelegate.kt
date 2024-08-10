package luyao.direct.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import luyao.direct.R
import luyao.direct.databinding.ItemToolPopBinding
import luyao.direct.model.entity.AddEntity

/**
 * author: luyao
 * date:   2021/10/8 15:03
 */
class EngineAddViewDelegate :
    ItemViewDelegate<AddEntity, EngineAddViewDelegate.ToolViewHolder>() {

    private var clickListener: ((View) -> Unit)? = null

    fun setClickListener(action: (View) -> Unit) {
        this.clickListener = action
    }

    override fun onBindViewHolder(holder: ToolViewHolder, item: AddEntity) {
        holder.binding.toolItem.setImageResource(R.drawable.ic_circlr_add)
//        Glide.with(holder.itemView).load(R.drawable.ic_circlr_add).into(holder.binding.toolItem)
        holder.binding.toolRoot.setOnClickListener { clickListener?.invoke(holder.itemView) }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ToolViewHolder {
        val binding = ItemToolPopBinding.inflate(LayoutInflater.from(context))
        return ToolViewHolder(binding)
    }

    class ToolViewHolder(val binding: ItemToolPopBinding) : RecyclerView.ViewHolder(binding.root)


}