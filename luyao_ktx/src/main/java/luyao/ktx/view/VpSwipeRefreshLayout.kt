package luyao.ktx.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintSet.Motion
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlin.math.abs

/**
 * Description: 解决 ViewPager2 嵌套 SwipeRefreshLayout
 * Author: luyao
 * Date: 2023/2/17 10:37
 */
class VpSwipeRefreshLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) :
    SwipeRefreshLayout(context, attrs) {

    private var downX = 0
    private var downY = 0
    private var beginScroll = false

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when(ev.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = ev.x.toInt()
                downY = ev.y.toInt()
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val endX = ev.x.toInt()
                val endY = ev.y.toInt()
                val disX  = abs(endX - downX)
                val disY = abs(endY - downY)
                if (disX > disY) {
                    if (!beginScroll) {
                        parent.requestDisallowInterceptTouchEvent(false)
                    } else {
                        beginScroll = true
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                }
            }
            MotionEvent.ACTION_UP,MotionEvent.ACTION_CANCEL -> {
                parent.requestDisallowInterceptTouchEvent(false)
                beginScroll = false
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}