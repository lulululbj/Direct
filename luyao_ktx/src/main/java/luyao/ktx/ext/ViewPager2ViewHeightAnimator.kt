package luyao.ktx.ext

import android.view.View
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import luyao.ktx.util.YLog

class ViewPager2ViewHeightAnimator {

    var viewPager2: ViewPager2? = null; set(value) {
        if (field != value) {
            field?.unregisterOnPageChangeCallback(onPageChangeCallback)
            field = value
            value?.registerOnPageChangeCallback(onPageChangeCallback)
        }
    }

    private val layoutManager: LinearLayoutManager? get() = (viewPager2?.getChildAt(0) as? RecyclerView)?.layoutManager as? LinearLayoutManager

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            recalculate(position, positionOffset)
        }
    }

    fun recalculate(position: Int, positionOffset: Float = 0f) = layoutManager?.apply {
//        YLog.e("position: $position offset: $positionOffset")
        val leftView = findViewByPosition(position) ?: return@apply
        val rightView = findViewByPosition(position + 1)
        val setMeasure = {
            viewPager2?.apply {
                val leftHeight = getMeasuredViewHeightFor(leftView)
//                YLog.i("$position leftHeight: $leftHeight")
                layoutParams = layoutParams.apply {
                    height = if (rightView != null) {
                        val rightHeight = getMeasuredViewHeightFor(rightView)
//                        YLog.i("$position rightHeight: $rightHeight")
                        leftHeight + ((rightHeight - leftHeight) * positionOffset).toInt()
                    } else {
//                        YLog.i("$position no right view")
                        leftHeight
                    }
//                    YLog.i("$position height: $height")
                }
//                invalidate()
            }
        }
        leftView.viewTreeObserver.addDisposableOnGlobalLayoutListener { setMeasure.invoke() }
        rightView?.viewTreeObserver?.addDisposableOnGlobalLayoutListener { setMeasure.invoke() }
        setMeasure.invoke()
    }

    private fun getMeasuredViewHeightFor(view: View): Int {
        val wMeasureSpec = View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY)
        val hMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(wMeasureSpec, hMeasureSpec)
        return view.measuredHeight
    }
}