package luyao.sdk.keyboard

import android.app.Activity
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.*
import android.widget.PopupWindow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import luyao.sdk.databinding.KeyboardPopupWindowBinding
import luyao.sdk.extension.getAppScreenHeight
import luyao.sdk.extension.getScreenHeight

/**
 * 监听键盘高度变化
 *  @author: luyao
 * @date: 2021/10/24 下午11:28
 */
class KeyboardChangeObserver(
    val activity: Activity,
    private val heightListener: ((Int) -> Unit)? = null
) : PopupWindow(activity), LifecycleObserver,
    ViewTreeObserver.OnGlobalLayoutListener {

    private val rootView: View = activity.findViewById(android.R.id.content)
    private val binding = KeyboardPopupWindowBinding.inflate(activity.layoutInflater)
    private var lastHeight = -1

    init {
        contentView = binding.root
        width = 0
        height = ViewGroup.LayoutParams.MATCH_PARENT
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        inputMethodMode = INPUT_METHOD_NEEDED
        softInputMode =
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun bind() {
        contentView.viewTreeObserver.addOnGlobalLayoutListener(this)
        rootView.post {
            if (!isShowing) {
                showAtLocation(rootView, Gravity.NO_GRAVITY, 0, 0)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun unBind() {
        if (isShowing) {
            dismiss()
        }
        contentView.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    override fun onGlobalLayout() {
        val screenHeight = activity.getScreenHeight()
        val rect = Rect()
        contentView.getWindowVisibleDisplayFrame(rect)
        val keyboardHeight = screenHeight - rect.bottom

        if (keyboardHeight != lastHeight) {
            Log.e("input", "keyboardHeight: $keyboardHeight")
            lastHeight = keyboardHeight
            heightListener?.invoke(keyboardHeight)
        }
    }

}