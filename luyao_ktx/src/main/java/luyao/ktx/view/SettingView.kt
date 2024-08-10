package luyao.ktx.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import com.hi.dhl.binding.viewbind
import luyao.ktx.R
import luyao.ktx.databinding.ViewSettingBinding

/**
 * Description:
 * Author: luyao
 * Date: 2023/6/8 15:58
 */
class SettingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr) {

    private val binding: ViewSettingBinding by viewbind()

    var settingClick: () -> Unit = {}

    init {
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingView)
        typedArray.let {
            binding.run {
                settingArrow.isVisible = it.getBoolean(R.styleable.SettingView_showRightArrow, true)
                settingTitle.text = it.getString(R.styleable.SettingView_settingText)
                rightText.text = it.getString(R.styleable.SettingView_rightText)
                it.getResourceId(R.styleable.SettingView_settingIcon, 0).run {
                    if (this != 0) settingIcon.setImageResource(this) else settingIcon.isVisible =
                        false
                }
                val settingPosition = it.getInt(R.styleable.SettingView_settingPosition, 0)
                root.setBackgroundResource(
                    when (settingPosition) {
                        0 -> R.drawable.bg_top_setting
                        1 -> R.drawable.bg_bottom_setting
                        2 -> R.drawable.bg_middle_setting
                        else -> R.drawable.bg_single_setting
                    }
                )
                root.setOnClickListener { settingClick.invoke() }
            }
        }
        typedArray.recycle()
    }

    fun setSettingText(text: String) {
        binding.settingTitle.text = text
    }

    fun setRightText(text: String) {
        binding.rightText.text = text
    }

    fun setBackground(@DrawableRes resId: Int) {
        binding.root.setBackgroundResource(resId)
    }
}