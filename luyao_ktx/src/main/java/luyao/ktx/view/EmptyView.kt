package luyao.ktx.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.hi.dhl.binding.viewbind
import luyao.ktx.R
import luyao.ktx.databinding.ViewEmptyBinding
import luyao.ktx.ext.dp2px

/**
 * Description:
 * Author: luyao
 * Date: 2023/2/7 16:55
 */
class EmptyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr) {

    private val binding: ViewEmptyBinding by viewbind()

    init {
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.EmptyView).use {
            binding.run {
                emptyImage.setImageResource(
                    it.getResourceId(
                        R.styleable.EmptyView_ev_emptyImage,
                        R.drawable.ic_clear_text
                    )
                )
                emptyText.run {
                    text = it.getString(R.styleable.EmptyView_ev_emptyText)
                    val dimenSize =
                        it.getDimensionPixelSize(R.styleable.EmptyView_ev_emptyTextSize, 16)
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, dimenSize.toFloat())
                    setTextColor(
                        it.getColor(
                            R.styleable.EmptyView_ev_emptyTextColor,
                            ContextCompat.getColor(context, R.color.black)
                        )
                    )
                }
            }

        }
    }

}