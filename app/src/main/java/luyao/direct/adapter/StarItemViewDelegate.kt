package luyao.direct.adapter

import android.content.Context
import android.view.ViewGroup
import com.drakeet.multitype.ViewDelegate
import luyao.direct.model.MMKVConstants
import luyao.direct.model.entity.RecentEntity
import luyao.direct.ui.DirectActivity
import luyao.direct.util.loadIcon
import luyao.direct.view.StarItemView
import luyao.ktx.util.TimerCounter

/**
 * Description:
 * Author: luyao
 * Date: 2023/4/4 17:09
 */
class StarItemViewDelegate(val activity: DirectActivity) : ViewDelegate<RecentEntity, StarItemView>() {
    override fun onBindView(view: StarItemView, item: RecentEntity) {
        item.loadIcon(view.imageView)
        view.textView.run {
            text = if (item.name.contains("-")) {
                item.name.substring(item.name.lastIndexOf("-") + 1)
            } else {
                item.name
            }
            checkScroll()
        }
        view.setOnClickListener {
            item.go()
            if (MMKVConstants.autoClose) {
                activity.finish()
            }
        }
        TimerCounter.end("Start1")
    }

    override fun onCreateView(context: Context) =
        StarItemView(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
}