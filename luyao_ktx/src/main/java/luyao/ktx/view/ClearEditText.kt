package luyao.ktx.view

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import luyao.ktx.R

/**
 * author: luyao
 * date:   2021/10/13 10:48
 */
class ClearEditText(context: Context, attrs: AttributeSet) :
    androidx.appcompat.widget.AppCompatEditText(context, attrs) {

    private var mClearDrawable: Drawable? = null

    init {
        context.obtainStyledAttributes(attrs, R.styleable.ClearEditText).run {
            mClearDrawable = getDrawable(R.styleable.ClearEditText_clearDrawable)
                ?: ContextCompat.getDrawable(context, R.drawable.ic_clear_text)
            recycle()
        }
    }

    public fun setClearDrawable(@DrawableRes drawableRes: Int) {
        setClearDrawable(ContextCompat.getDrawable(context, drawableRes))
    }

    private fun setClearDrawable(drawable: Drawable?) {
        mClearDrawable = drawable
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        // 有焦点并且文字不为空时显示 clear 图标
        showClear(isFocusable && (text?.isNotEmpty() ?: false))
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        showClear(focused && (text?.isNotEmpty() ?: false))
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            if (isTouchClear(event.x)) {
                text = null
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performContextClick(x: Float, y: Float): Boolean {
        return !isTouchClear(x) && super.performContextClick(x, y)
    }

    // 是否触摸到 clear 图标
    private fun isTouchClear(x: Float): Boolean {
        return compoundDrawables[2] != null && x >= width - compoundPaddingRight && x < width
    }


    private fun showClear(visible: Boolean) {
        setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            if (visible) mClearDrawable else null,
            null
        )
    }

}