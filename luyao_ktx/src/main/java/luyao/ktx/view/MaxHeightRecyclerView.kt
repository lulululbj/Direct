package luyao.ktx.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import luyao.ktx.ext.getScreenHeight

/**
 *  @author: luyao
 * @date: 2021/9/4 上午8:27
 */
class MaxHeightRecyclerView(context: Context, attrs: AttributeSet) : RecyclerView(context, attrs) {

    private var maxHeight = context.getScreenHeight()

    fun setMaxHeight(height: Int) {
        maxHeight = height
//        requestLayout()
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        var maxHeightSpec = heightSpec
        if (maxHeight > 0)
            maxHeightSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST)
        super.onMeasure(widthSpec, maxHeightSpec)
    }
}