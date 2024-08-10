package luyao.ktx.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import luyao.ktx.util.YLog

/**
 * Description:
 * Author: luyao
 * Date: 2023/2/7 15:52
 */
class LyRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    RecyclerView(context, attrs, defStyleAttr) {

    var emptyView: View? = null

    private val dataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            YLog.e("data changed")
            if (adapter != null && emptyView != null) {
                if (adapter?.itemCount == 0) {
                    emptyView?.isVisible = true
                    this@LyRecyclerView.isVisible = false
                } else {
                    emptyView?.isVisible = false
                    this@LyRecyclerView.isVisible = true
                }
            }
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(dataObserver)
    }

}