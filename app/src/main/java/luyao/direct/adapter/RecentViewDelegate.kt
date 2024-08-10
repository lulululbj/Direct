package luyao.direct.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import luyao.direct.R
import luyao.direct.model.entity.RecentEntity
import luyao.direct.util.loadIcon
import luyao.ktx.util.TimerCounter
import luyao.view.MarqueeTextView

/**
 *  @author: luyao
 * @date: 2021/8/27 下午11:43
 */
class RecentViewDelegate(
    val click: ((View, RecentEntity) -> Unit)? = null,
    val longClick: ((RecentEntity) -> Unit)? = null
) : ItemViewDelegate<RecentEntity, RecentViewDelegate.ViewHolder>() {

    var isEditMode = false

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_recent, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: RecentEntity) {
        item.loadIcon(holder.appIcon)
//        holder.appName.setTextColor(DirectApp.App.getProperTextColor())
        holder.recentRoot.setOnClickListener {
            click?.invoke(it, item)
        }
        holder.recentRoot.setOnLongClickListener {
            longClick?.invoke(item)
            true
        }
        holder.appName.run {
            text = if (item.name.contains("-")) {
                item.name.substring(item.name.lastIndexOf("-") + 1)
            } else {
                item.name
            }
            checkScroll()
        }
        TimerCounter.end("Start1")
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appIcon: ImageView = itemView.findViewById(R.id.recentIcon)
        val appName: MarqueeTextView = itemView.findViewById(R.id.recentName)
        val recentRoot: LinearLayout = itemView.findViewById(R.id.recentRoot)
    }
}