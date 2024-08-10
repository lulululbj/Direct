package luyao.ktx.ext

import android.animation.ValueAnimator
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import luyao.ktx.util.YLog

//计算fragment的高度并设置给viewPager
fun ViewPager2.updatePagerHeightForChild(view: View?) {
    view?.post {
        val wMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY)
        val hMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(wMeasureSpec, hMeasureSpec)
        if (layoutParams.height != view.measuredHeight) {
            val anim = ValueAnimator.ofInt(
                layoutParams.height,
                view.measuredHeight
            )
            anim.addUpdateListener {
                val height = it.animatedValue as Int
                layoutParams = layoutParams.also { lp ->
                    lp.height = height
                }
            }
            anim.duration = 200
            anim.start()
            YLog.e("updatePagerHeight: ${view.measuredHeight}")
        }
    }
}

