package luyao.direct.view

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * Description:
 * Author: luyao
 * Date: 2023/4/6 15:49
 */
class KtxItemTouchHelperCallback(
    private val dragFlags: Int = 0,
    private val swipeFlags: Int = 0,
    private val dataList: List<*>,
    val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
    private val isLongPressDragEnabled: Boolean = true,
    private val isItemViewSwipeEnabled: Boolean = false,
    private val onClearView: (() -> Unit)? = null
) : ItemTouchHelper.Callback() {
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ) = makeMovementFlags(dragFlags, swipeFlags)

    override fun isLongPressDragEnabled() = isLongPressDragEnabled

    override fun isItemViewSwipeEnabled() = isItemViewSwipeEnabled

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val fromPosition = viewHolder.bindingAdapterPosition
        val toPosition = target.bindingAdapterPosition
        var i = fromPosition
        if (fromPosition < toPosition) {
            while (i < toPosition) {
                Collections.swap(dataList, i, i + 1)
                i++
            }
        } else {
            while (i > toPosition) {
                Collections.swap(dataList, i, i - 1)
                i--
            }
        }
        adapter.notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        onClearView?.invoke()
    }
}