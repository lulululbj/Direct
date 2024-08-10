package luyao.ktx.web

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import luyao.ktx.R

/**
 * Description:
 * Author: luyao
 * Date: 2022/8/11 13:50
 */
class WebTitle(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    private val view = View.inflate(context, R.layout.web_title_layout, this)
    private val webTitleRoot = view.findViewById<ConstraintLayout>(R.id.webTitleRoot)
    private val leftImage = view.findViewById<ImageView>(R.id.leftImage)
    private val rightImage = view.findViewById<ImageView>(R.id.rightImage)
    private val leftTitle = view.findViewById<TextView>(R.id.leftTitle)
    private val rightTitle = view.findViewById<TextView>(R.id.rightTitle)
    private val title = view.findViewById<TextView>(R.id.title)

    private var webLauncher: WebLauncher? = null
    private var onLeftTitleClick: (() -> Unit)? = null
    private var onRightTitleClick: (() -> Unit)? = null
    private var onTitleClick: (() -> Unit)? = null

    init {
        initView()
    }

    private fun initView() {
        webLauncher?.let {
            webTitleRoot.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    it.titleBackgroundColor
                )
            )

            leftTitle.run {
                text = it.leftText
                setTextSize(TypedValue.COMPLEX_UNIT_PX, it.leftTextSize)
                setTextColor(ContextCompat.getColor(context, it.leftTextColor))
                setOnClickListener { onLeftTitleClick?.invoke() }
            }

            rightTitle.run {
                text = it.rightText
                setTextSize(TypedValue.COMPLEX_UNIT_PX, it.rightTextSize)
                setTextColor(ContextCompat.getColor(context, it.rightTextColor))
                setOnClickListener { onRightTitleClick?.invoke() }
            }

            leftImage.setImageResource(it.leftIcon)
            leftImage.setOnClickListener { onLeftTitleClick?.invoke() }

            title.run {
                text = it.title
                setTextSize(TypedValue.COMPLEX_UNIT_PX, it.titleTextSize)
                setTextColor(it.titleTextColor)
                setOnClickListener { onTitleClick?.invoke() }
            }

            if (it.rightIcon == null) {
                rightImage.isVisible = false
            } else {
                rightImage.isVisible = true
                rightImage.setImageResource(it.rightIcon!!)
                rightImage.setOnClickListener { onRightTitleClick?.invoke() }
            }
        }
    }

    fun setWebLauncher(webLauncher: WebLauncher?) {
        this.webLauncher = webLauncher
        initView()
    }

    fun setLeftTitleClick(listener: () -> Unit) {
        onLeftTitleClick = listener
    }

    fun setRightTitleClick(listener: () -> Unit) {
        onRightTitleClick = listener
    }

    fun setTitleClick(listener: () -> Unit) {
        onTitleClick = listener
    }

    fun setTitle(title: String) {
        this.title.text = title
    }
}