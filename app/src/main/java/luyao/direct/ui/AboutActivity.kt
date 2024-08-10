package luyao.direct.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.drakeet.about.AbsAboutActivity
import com.drakeet.about.Category
import com.drakeet.about.Contributor
import com.drakeet.about.License
import com.google.android.material.appbar.CollapsingToolbarLayout
import luyao.direct.R
import luyao.direct.ext.joinQQGroup
import luyao.ktx.ext.versionName
import java.util.*

/**
 *  @author: luyao
 * @date: 2021/9/2 下午11:04
 */
class AboutActivity : AbsAboutActivity() {

    val BSD3 = "BSD 3-Clause License"


    override fun onTitleViewCreated(collapsingToolbar: CollapsingToolbarLayout) {
        super.onTitleViewCreated(collapsingToolbar)
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.title_text_color))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
//        val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView)
//        windowInsetsController?.isAppearanceLightStatusBars = true
//        window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.white)
        super.onCreate(savedInstanceState)

        setOnContributorClickedListener { _, contributor ->
            var result = false
            if (contributor.name == "Q群") {
                joinQQGroup()
                result = true
            }
            result
        }
    }

    override fun onCreateHeader(icon: ImageView, slogan: TextView, version: TextView) {
        icon.setImageResource(R.mipmap.ic_launcher)
        slogan.setText(R.string.slogan)
        version.text = String.format(Locale.getDefault(), "V%s", versionName)
    }

    override fun onItemsCreated(items: MutableList<Any>) {
        items.run {
            add(Category(getString(R.string.developer)))
            add(Contributor(R.drawable.luyao, "路遥", "Developer", "https://luyao.tech"))
            add(Contributor(R.drawable.ic_wechat, "公众号", "路遥TM"))
            add(Category("意见反馈"))
            add(
                Contributor(
                    R.drawable.ic_gmail,
                    getString(R.string.Email),
                    "sunluyao1993x@gmail.com",
                    "mailto://sunluyao1993x@gmail.com"
                )
            )
            add(Contributor(R.drawable.ic_qq, "Q群", "直达和它的用户们"))
            add(Category(getString(R.string.open_licenses)))
            add(
                License(
                    "about-page",
                    "drakeet",
                    License.APACHE_2,
                    "https://github.com/PureWriter/about-page"
                )
            )
            add(
                License(
                    "AndroidX",
                    "Google",
                    License.APACHE_2,
                    "https://github.com/androidx"
                )
            )
            add(
                License(
                    "leakcanary",
                    "Square",
                    License.APACHE_2,
                    "https://github.com/square/leakcanary"
                )
            )
            add(
                License(
                    "Kotlin",
                    "JetBrains",
                    License.APACHE_2,
                    "https://github.com/JetBrains/kotlin"
                )
            )
            add(
                License(
                    "material-components-android",
                    "Google",
                    License.APACHE_2,
                    "https://github.com/material-components/material-components-android"
                )
            )
            add(
                License(
                    "material-intro-screen",
                    "TangoAgency",
                    License.MIT,
                    "https://github.com/TangoAgency/material-intro-screen"
                )
            )
            add(
                License(
                    "MultiType",
                    "drakeet",
                    License.APACHE_2,
                    "https://github.com/drakeet/MultiType"
                )
            )
            add(
                License(
                    "MMKV",
                    "Tencent",
                    BSD3,
                    "https://github.com/Tencent/MMKV"
                )
            )
            add(
                License(
                    "PermissionX",
                    "guolin",
                    License.APACHE_2,
                    "https://github.com/guolindev/PermissionX"
                )
            )
        }
    }
}