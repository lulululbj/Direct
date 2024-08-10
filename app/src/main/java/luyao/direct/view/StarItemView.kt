package luyao.direct.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import luyao.direct.R
import luyao.ktx.ext.dp2px
import luyao.view.MarqueeTextView

/**
 * Description:
 * Author: luyao
 * Date: 2023/4/4 16:47
 */
class StarItemView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

     lateinit var imageView: AppCompatImageView
     lateinit var textView: MarqueeTextView

    init {
        initView()
    }

    private fun initView() {
        orientation = VERTICAL

        imageView = AppCompatImageView(context).apply {
            val imageSize = dp2px(26).toInt()
            layoutParams = LayoutParams(imageSize, imageSize)
        }
        addView(imageView)

        textView = MarqueeTextView(context).apply {
            layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    topMargin = dp2px(4).toInt()
                }
            setTextColor(ContextCompat.getColor(context, R.color.grey))
            textSize = dp2px(11)
            setPadding(dp2px(2).toInt(), 0, dp2px(2).toInt(), 0)
        }
        addView(textView)

    }
}