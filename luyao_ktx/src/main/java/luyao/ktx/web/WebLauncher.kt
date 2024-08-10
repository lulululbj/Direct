package luyao.ktx.web

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import luyao.ktx.R
import luyao.ktx.app.AppInit
import luyao.ktx.ext.dp2px
import java.io.Serializable

/**
 * Description:
 * Author: luyao
 * Date: 2022/8/11 11:18
 */
class WebLauncher : Serializable {

    var url: String = ""

    var title: String = ""
    var titleTextSize = dp2px(20)

    @ColorRes
    var titleTextColor: Int = R.color.white
    var leftText: String = ""
    var leftTextSize = dp2px(18)

    @ColorRes
    var leftTextColor: Int = R.color.webview_title_text
    var rightText: String = ""
    var rightTextSize = dp2px(18)

    @ColorRes
    var rightTextColor: Int = R.color.webview_right_text

    @ColorRes
    var titleBackgroundColor: Int = R.color.webview_title_background

    @DrawableRes
    var leftIcon: Int = R.drawable.ic_back_white

    @DrawableRes
    var rightIcon: Int? = null

    var postData: String = ""

    fun url(url: String) {
        this.url = url
    }

    fun titleBackgroundColor(@ColorRes color: Int) = apply {
        titleBackgroundColor = color
    }

    fun title(@StringRes res: Int): WebLauncher = apply {
        title = AppInit.application.getString(res)
    }

    fun title(text: String): WebLauncher = apply {
        title = text
    }

    fun titleTextSize(textSize: Int): WebLauncher = apply {
        titleTextSize = dp2px(textSize)
    }

    fun titleTextColor(@ColorRes color: Int) = apply {
        titleTextColor = color
    }

    fun leftText(@StringRes res: Int): WebLauncher = apply {
        leftText = AppInit.application.getString(res)
    }

    fun leftText(text: String): WebLauncher = apply {
        leftText = text
    }

    fun leftTextSize(textSize: Int): WebLauncher = apply {
        leftTextSize = dp2px(textSize)
    }

    fun leftTextColor(@ColorRes color: Int) = apply {
        leftTextColor = color
    }

    fun rightText(@StringRes res: Int): WebLauncher = apply {
        rightText = AppInit.application.getString(res)
    }

    fun rightText(text: String): WebLauncher = apply {
        rightText = text
    }

    fun rightTextSize(textSize: Int): WebLauncher = apply {
        rightTextSize = dp2px(textSize)
    }

    fun rightTextColor(@ColorRes color: Int) = apply {
        rightTextColor = color
    }

    fun leftIcon(@DrawableRes res: Int): WebLauncher = apply {
        leftIcon = res
    }

    fun rightIcon(@DrawableRes res: Int): WebLauncher = apply {
        rightIcon = res
    }

    fun postData(data: String) {
        postData = data
    }

    inline fun start(context: Context, func: WebLauncher.() -> Unit): WebLauncher = apply {
        this.func()
        this.start(context)
    }

    companion object {
        const val name = "webLauncher"
    }

    fun start(context: Context) {
        context.startActivity(Intent(context, WebActivity::class.java).apply {
            putExtra(name, this@WebLauncher)
        })
    }

}