package luyao.direct.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import luyao.direct.DirectApp
import luyao.direct.R
import luyao.direct.databinding.ItemTagEngineBinding
import luyao.direct.model.entity.NewDirectEntity
import luyao.direct.util.loadIcon
import luyao.direct.view.getProperDirectDrawable
import java.util.Locale

/**
 * author: luyao
 * date:   2021/11/9 17:08
 */
class TagEngineViewDelegate :
    ItemViewDelegate<NewDirectEntity, TagEngineViewDelegate.ViewHolder>() {

    private var itemClickListener: ((NewDirectEntity) -> Unit)? = null

    fun setItemClickListener(itemClickListener: ((NewDirectEntity) -> Unit)?) {
        this.itemClickListener = itemClickListener
    }

    override fun onBindViewHolder(holder: ViewHolder, item: NewDirectEntity) {
        holder.run {
            binding.engineRoot.setBackgroundResource(
                getProperDirectDrawable(
                    item.isFirst,
                    item.isLast
                )
            )
            item.loadIcon(binding.engineIcon)
//            binding.engineName.setTextColor(DirectApp.App.getProperTextColor())
            binding.engineName.text = String.format(
                Locale.getDefault(),
                DirectApp.App.getString(R.string.search_by),
                item.label
            )
            binding.engineRoot.setOnClickListener { itemClickListener?.invoke(item) }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, item: NewDirectEntity, payloads: List<Any>) {
        if (payloads.isNotEmpty()) {
            holder.binding.engineRoot.setBackgroundResource(
                getProperDirectDrawable(
                    item.isFirst,
                    item.isLast
                )
            )
        } else {
            onBindViewHolder(holder, item)
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        val binding = ItemTagEngineBinding.inflate(LayoutInflater.from(context))
        return ViewHolder(binding)
    }

    class ViewHolder(val binding: ItemTagEngineBinding) : RecyclerView.ViewHolder(binding.root)
}