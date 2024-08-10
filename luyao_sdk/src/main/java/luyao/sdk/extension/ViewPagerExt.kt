package luyao.sdk.extension

import androidx.viewpager.widget.ViewPager

/**
 *  @author: luyao
 * @date: 2021/11/7 上午7:21
 */
fun ViewPager.onPageSelected(action: (Int) -> Unit) {
    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {

        }

        override fun onPageSelected(position: Int) {
            action.invoke(position)
        }

        override fun onPageScrollStateChanged(state: Int) {
        }
    })
}