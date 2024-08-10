package luyao.ktx.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlin.math.abs

/**
 * Description:
 * Author: luyao
 * Date: 2023/2/17 16:30
 */
class SwipeVpRefreshLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) :
    SwipeRefreshLayout(context, attrs) {

    private var downX = 0f
    private var downY = 0f
    private var isMoveHorizontal = false
    private val mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when(ev.action) {
            MotionEvent.ACTION_DOWN -> {
                isMoveHorizontal = false
                downX = ev.x
                downY = ev.y
            }
            MotionEvent.ACTION_MOVE -> {
                if (isMoveHorizontal)
                    return false
                val distanceX = abs(ev.x - downX)
                val distanceY = abs(ev.y - downY)
                if (distanceX > mTouchSlop && distanceX > distanceY) {
                    isMoveHorizontal = true
                    return false
                }
            }
            MotionEvent.ACTION_UP,MotionEvent.ACTION_CANCEL -> {
                isMoveHorizontal = false
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
}