package luyao.direct.ext

import android.app.Activity
import android.graphics.Insets
import android.view.View
import android.view.ViewGroup
import androidx.core.view.*

/**
 * author: luyao
 * date:   2021/10/27 15:03
 */
class InitialPadding(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
)

fun recordInitialPaddingForView(view: View) =
    InitialPadding(view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom)

class InitialMargin(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
)

fun recordInitialMarginForView(view: View) =
    InitialMargin(view.marginLeft, view.marginTop, view.marginRight, view.marginBottom)

fun View.doOnApplyWindowInsets(block: (WindowInsetsCompat, InitialPadding, InitialMargin) -> Unit) {
    val initialPadding = recordInitialPaddingForView(this)
    val initialMargin = recordInitialMarginForView(this)
    ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
        block(insets, initialPadding, initialMargin)
        insets
    }
}

fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        requestApplyInsets()
    } else {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) {

            }

        })
    }
}

fun View.applyBottomWindowInsetForScrollingView(scrollingView: ViewGroup) {

}

fun Activity.isGestureNavigation(navigationBarInsets: Insets): Boolean {
    val threshold = (20 * resources.displayMetrics.density).toInt()
    return navigationBarInsets.bottom < threshold.coerceAtLeast(44)
}