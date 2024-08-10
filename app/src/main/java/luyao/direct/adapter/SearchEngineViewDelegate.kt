package luyao.direct.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.drakeet.multitype.ItemViewDelegate
import luyao.direct.R
import luyao.direct.model.entity.NewDirectEntity

/**
 *  @author: luyao
 * @date: 2021/9/19 上午12:19
 */
class SearchEngineViewDelegate :
    ItemViewDelegate<NewDirectEntity, SearchEngineViewDelegate.ViewHolder>() {

    private var itemTouchHelper: ItemTouchHelper? = null

    fun setOnDragListener(touchHelper: ItemTouchHelper) {
        itemTouchHelper = touchHelper
    }

    override fun onBindViewHolder(holder: ViewHolder, item: NewDirectEntity) {
        Glide.with(holder.engineName).load(item.iconUrl).into(holder.engineImage)
//        entityResIds[item.id]?.let { holder.engineImage.setImageResource(it) }
        holder.engineName.text = item.label
//        itemTouchHelper?.let {
//            holder.engineRoot.setOnTouchListener { _, event ->
//                if (event.action == MotionEvent.ACTION_DOWN) {
//                    it.startDrag(holder)
//                }
//                false
//            }
//        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_search_engine, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val engineImage = itemView.findViewById<ImageView>(R.id.engineIcon)
        val engineName = itemView.findViewById<TextView>(R.id.engineName)
        val engineRoot = itemView.findViewById<ConstraintLayout>(R.id.engineRoot)
    }
}