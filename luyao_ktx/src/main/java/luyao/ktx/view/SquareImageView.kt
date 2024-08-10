package luyao.ktx.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.imageview.ShapeableImageView
import luyao.ktx.R

/**
 * Description:
 * Author: luyao
 * Date: 2023/6/2 10:55
 */
class SquareImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    ShapeableImageView(context, attrs, defStyleAttr) {

    private var hwRatio = 1f

    init {
        context.obtainStyledAttributes(R.styleable.SquareImageView).run {
            hwRatio = getFloat(R.styleable.SquareImageView_hwRatio, 0f)
            recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)

//        val width = measuredWidth
//        val height = measuredHeight
//
//        val calculatedHeight: Int = calculateHeightByRatio(width)
//
//        if (calculatedHeight != height) {
//            setMeasuredDimension(width, calculatedHeight)
//        }
    }

    private fun calculateHeightByRatio(side: Int): Int {
        return (hwRatio * side.toFloat()).toInt()
    }
}