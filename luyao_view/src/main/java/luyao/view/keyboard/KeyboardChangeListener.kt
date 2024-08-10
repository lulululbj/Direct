package luyao.view.keyboard

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import luyao.ktx.ext.getScreenHeight

/**
 *  @author: luyao
 * @date: 2021/7/3 上午7:45
 */
class KeyboardChangeListener(private val activity: Activity) :
    ViewTreeObserver.OnGlobalLayoutListener , LifecycleObserver{

    private val MIN_KEYBOARD_HEIGHT = 300
    private val mContentView by lazy { activity.findViewById<View>(android.R.id.content) }
    private val mWindow by lazy { activity.window }
    private var mListener: ((Boolean, Int) -> Unit)? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun init() {
        mContentView.viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun stop(){
        mContentView.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    fun addListener(listener: (Boolean, Int) -> Unit) {
        mListener = listener
    }

    override fun onGlobalLayout() {
        val contentViewHeight = mContentView.height
        if (contentViewHeight == 0) return

        val screenHeight = activity.getScreenHeight()

        val rect = Rect()
        mWindow.decorView.getWindowVisibleDisplayFrame(rect)
        val keyboardHeight = screenHeight - rect.bottom

        mListener?.invoke(keyboardHeight > MIN_KEYBOARD_HEIGHT, keyboardHeight)
    }


}