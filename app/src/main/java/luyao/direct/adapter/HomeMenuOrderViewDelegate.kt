package luyao.direct.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import luyao.direct.databinding.ItemHomeMenuOrderBinding
import luyao.direct.model.entity.HomeMenu

class HomeMenuOrderViewDelegate :
    ItemViewDelegate<HomeMenu, HomeMenuOrderViewDelegate.ViewHolder>() {

    private var itemTouchHelper: ItemTouchHelper? = null

    fun setOnDragListener(touchHelper: ItemTouchHelper?) {
        itemTouchHelper = touchHelper
    }

    class ViewHolder(val binding: ItemHomeMenuOrderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ViewHolder, item: HomeMenu) {
        holder.binding.run {
            menuName.text = item.name
            itemTouchHelper?.let {
                root.setOnTouchListener { _, event ->
                    if (itemTouchHelper != null && event.action == MotionEvent.ACTION_DOWN) {
                        it.startDrag(holder)
                    }
                    false
                }
            }
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup) =
        ViewHolder(
            ItemHomeMenuOrderBinding.inflate(LayoutInflater.from(context), parent, false)
        )
}