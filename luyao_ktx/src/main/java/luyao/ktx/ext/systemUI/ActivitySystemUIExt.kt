package luyao.ktx.ext.systemUI

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.util.Size
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import luyao.ktx.R

/**
 * Description:
 * Author: luyao
 * Date: 2023/7/5 09:36
 */
fun Activity.setLightStatusBar(isLightingColor: Boolean) {
    val window = this.window
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (isLightingColor) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }
}

fun Activity.setLightNavigationBar(isLightingColor: Boolean) {
    val window = this.window
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isLightingColor) {
        window.decorView.systemUiVisibility =
            window.decorView.systemUiVisibility or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            } else {
                0
            }
    }
}

/**
 * 必须在 Activity 的 onCreate 时调用
 */
fun Activity.immersiveStatusBar() {
    val view = (window.decorView as ViewGroup).getChildAt(0)
    view.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
        val lp = view.layoutParams as FrameLayout.LayoutParams
        if (lp.topMargin > 0) {
            lp.topMargin = 0
            v.layoutParams = lp
        }
        if (view.paddingTop > 0) {
            view.setPadding(0, 0, 0, view.paddingBottom)
            val content = findViewById<View>(android.R.id.content)
            content.requestLayout()
        }
    }

    val content = findViewById<View>(android.R.id.content)
    content.setPadding(0, 0, 0, content.paddingBottom)

    window.decorView.findViewById(R.id.status_bar_view) ?: View(window.context).apply {
        id = R.id.status_bar_view
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            statusHeight
        )
        params.gravity = Gravity.TOP
        layoutParams = params
        (window.decorView as ViewGroup).addView(this)

        (window.decorView as ViewGroup).setOnHierarchyChangeListener(object :
            ViewGroup.OnHierarchyChangeListener {
            override fun onChildViewAdded(parent: View?, child: View?) {
                if (child?.id == android.R.id.statusBarBackground) {
                    child.scaleX = 0f
                }
            }

            override fun onChildViewRemoved(parent: View?, child: View?) {

            }
        })
    }
    setStatusBarColor(Color.TRANSPARENT)
}

/**
 * 必须在 Activity 的 onCreate 时调用
 */
fun Activity.immersiveNavigationBar(callback: (() -> Unit)? = null) {
    val view = (window.decorView as ViewGroup).getChildAt(0)
    view.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
        val lp = view.layoutParams as FrameLayout.LayoutParams
        if (lp.bottomMargin > 0) {
            lp.bottomMargin = 0
            v.layoutParams = lp
        }
        if (view.paddingBottom > 0) {
            view.setPadding(0, view.paddingTop, 0, 0)
            val content = findViewById<View>(android.R.id.content)
            content.requestLayout()
        }
    }

    val content = findViewById<View>(android.R.id.content)
    content.setPadding(0, content.paddingTop, 0, -1)

    val heightLiveData = MutableLiveData<Int>()
    heightLiveData.value = 0
    window.decorView.setTag(R.id.navigation_height_live_data, heightLiveData)
    callback?.invoke()

    window.decorView.findViewById(R.id.navigation_bar_view) ?: View(window.context).apply {
        id = R.id.navigation_bar_view
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            heightLiveData.value ?: 0
        )
        params.gravity = Gravity.BOTTOM
        layoutParams = params
        (window.decorView as ViewGroup).addView(this)

        if (this@immersiveNavigationBar is FragmentActivity) {
            heightLiveData.observe(this@immersiveNavigationBar) {
                val lp = layoutParams
                lp.height = heightLiveData.value ?: 0
                layoutParams = lp
            }
        }

        (window.decorView as ViewGroup).setOnHierarchyChangeListener(object :
            ViewGroup.OnHierarchyChangeListener {

            override fun onChildViewAdded(parent: View?, child: View?) {
                if (child?.id == android.R.id.navigationBarBackground) {
                    child.scaleX = 0f
                    bringToFront()
                    child.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                        heightLiveData.value = bottom - top
                    }
                } else if (child?.id == android.R.id.statusBarBackground) {
                    child.scaleX = 0f
                }
            }

            override fun onChildViewRemoved(parent: View?, child: View?) {

            }

        })
    }
    setNavigationBarColor(Color.TRANSPARENT)
}

/**
 * 当设置了 immersiveStatusBar 时，如需使用状态栏，可调用该函数
 */
fun Activity.fitStatusBar(fit: Boolean) {
    val content = findViewById<View>(android.R.id.content)
    if (fit) {
        content.setPadding(0, statusHeight, 0, content.paddingBottom)
    } else {
        content.setPadding(0, 0, 0, content.paddingBottom)
    }
}

fun Activity.fitNavigationBar(fit: Boolean) {
    val content = findViewById<View>(android.R.id.content)
    if (fit) {
        content.setPadding(0, content.paddingTop, 0, navigationBarHeightLiveData.value ?: 0)
    } else {
        content.setPadding(0, content.paddingTop, 0, -1)
    }
    if (this is FragmentActivity) {
        navigationBarHeightLiveData.observe(this) {
            if (content.paddingBottom != -1) {
                content.setPadding(0, content.paddingTop, 0, it)
            }
        }
    }
}

val Activity.isImmersiveStatusBar: Boolean
    get() = window.attributes.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION != 0

val Activity.statusHeight: Int
    get() {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId)
        }
        return 0
    }

val Activity.navigationHeight: Int
    get() = navigationBarHeightLiveData.value ?: 0

val Activity.screenSize: Size
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Size(
                windowManager.currentWindowMetrics.bounds.width(),
                windowManager.currentWindowMetrics.bounds.height()
            )
        } else {
            Size(windowManager.defaultDisplay.width, windowManager.defaultDisplay.height)
        }
    }

fun Activity.setStatusBarColor(color: Int) {
    val statusBarView = window.decorView.findViewById<View?>(R.id.status_bar_view)
    if (color == 0 && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        statusBarView?.setBackgroundColor(STATUS_BAR_MASK_COLOR)
    } else {
        statusBarView?.setBackgroundColor(color)
    }
}

fun Activity.setNavigationBarColor(color: Int) {
    val navigationBarView = window.decorView.findViewById<View?>(R.id.navigation_bar_view)
    if (color == 0 && Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
        navigationBarView?.setBackgroundColor(STATUS_BAR_MASK_COLOR)
    } else {
        navigationBarView?.setBackgroundColor(color)
    }
}

val Activity.navigationBarHeightLiveData: LiveData<Int>
    get() {
        var liveData = window.decorView.getTag(R.id.navigation_height_live_data) as? LiveData<Int>
        if (liveData == null) {
            liveData = MutableLiveData()
            window.decorView.setTag(R.id.navigation_height_live_data, liveData)
        }
        return liveData
    }

val Activity.screenWidth: Int
    get() = screenSize.width

val Activity.screenHeight: Int
    get() = screenSize.height

private const val STATUS_BAR_MASK_COLOR = 0x7F000000