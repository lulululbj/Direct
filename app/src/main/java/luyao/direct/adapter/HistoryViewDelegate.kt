package luyao.direct.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import luyao.direct.databinding.ItemHistoryBinding
import luyao.direct.model.entity.SearchHistoryEntity

/**
 *  @author: luyao
 * @date: 2021/10/21 上午12:02
 */
class HistoryViewDelegate :
    ItemViewDelegate<SearchHistoryEntity, HistoryViewDelegate.ViewHolder>() {

    private var itemLongClickListener: ((View, Int, SearchHistoryEntity) -> Unit)? = null
    private var itemClickListener: ((View, Int, SearchHistoryEntity) -> Unit)? = null

    fun setItemLongClickListener(itemLongClickListener: ((View, Int, SearchHistoryEntity) -> Unit)?) {
        this.itemLongClickListener = itemLongClickListener
    }

    fun setItemClickListener(itemClickListener: ((View, Int, SearchHistoryEntity) -> Unit)?) {
        this.itemClickListener = itemClickListener
    }

    override fun onBindViewHolder(holder: ViewHolder, item: SearchHistoryEntity) {
        holder.binding.run {
//            historyRoot.background = cornerPanelBgDrawable()
//            historyName.setTextColor(DirectApp.App.getProperTextColor())
//            historyIcon.setImageResource(R.drawable.ic_history)
            historyName.text = item.keyWord
            historyRoot.setOnLongClickListener {
                itemLongClickListener?.invoke(it, holder.layoutPosition, item)
                true
            }
            historyBt.setOnClickListener {
                itemLongClickListener?.invoke(it, holder.layoutPosition, item)
            }
            historyRoot.setOnClickListener {
                itemClickListener?.invoke(it, holder.layoutPosition, item)
            }
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(context))
        return ViewHolder(binding)
    }

    class ViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)
}