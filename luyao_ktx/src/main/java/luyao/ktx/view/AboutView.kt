package luyao.ktx.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.hi.dhl.binding.viewbind
import luyao.ktx.R
import luyao.ktx.databinding.ViewAboutBinding

/**
 * Description:
 * Author: luyao
 * Date: 2023/6/16 16:58
 */
class AboutView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: ViewAboutBinding by viewbind()

    var aboutClick: () -> Unit = {}

    init {
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AboutView)
        typedArray.let {
            binding.run {
                name.text = it.getString(R.styleable.AboutView_aboutText)
                desc.text = it.getString(R.styleable.AboutView_aboutDesc)
                avatar.setImageResource(
                    it.getResourceId(
                        R.styleable.AboutView_aboutIcon,
                        R.drawable.ic_about
                    )
                )
                val aboutPosition = it.getInt(R.styleable.AboutView_aboutPosition, 0)
                root.setBackgroundResource(
                    when (aboutPosition) {
                        0 -> R.drawable.bg_top_setting
                        1 -> R.drawable.bg_bottom_setting
                        2 -> R.drawable.bg_middle_setting
                        else -> R.drawable.bg_single_setting
                    }
                )
                root.setOnClickListener { aboutClick.invoke() }
            }
        }
        typedArray.recycle()
    }
}