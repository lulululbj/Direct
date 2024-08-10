package luyao.ktx.util

import androidx.recyclerview.widget.DiffUtil
import java.util.*

/**
 * author: luyao
 * date:   2021/7/13 17:51
 */
class MultiDiffCallback(private val oldItems: List<Any>, private val newItems: List<Any>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldItems.size
    }

    override fun getNewListSize(): Int {
        return newItems.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]
        return oldItem == newItem

    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]
        return Objects.equals(oldItem, newItem)
    }
}
