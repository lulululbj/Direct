package luyao.ktx.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/**
 * Description: 解决嵌套 Rv 导致外层 item 点击事件不响应
 * Author: luyao
 * Date: 2023/2/8 20:53
 */
class NoTouchNestRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    RecyclerView(context, attrs, defStyleAttr) {

    override fun onTouchEvent(e: MotionEvent?) = false

    override fun onInterceptTouchEvent(e: MotionEvent?) = true

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }
}