package luyao.ktx.web

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.webkit.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.hi.dhl.binding.viewbind
import luyao.ktx.base.BaseActivity
import luyao.ktx.databinding.ActivityWebBinding
import luyao.ktx.util.YLog


/**
 * Description:
 * Author: luyao
 * Date: 2022/8/21 16:08
 */
class WebActivity : BaseActivity() {

    private val binding: ActivityWebBinding by viewbind()
    private var ytoWebLauncher: WebLauncher? = null
    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    val chooseLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri == null) return@registerForActivityResult
            filePathCallback?.onReceiveValue(arrayOf(uri))
        }


    override fun initView() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        configRootInset(binding.root)
        initWebView()

        ytoWebLauncher = intent.getSerializableExtra(WebLauncher.name) as WebLauncher?
        ytoWebLauncher?.let {
            binding.run {
                toolBar.setBackgroundColor(
                    ContextCompat.getColor(
                        this@WebActivity,
                        it.titleBackgroundColor
                    )
                )
                toolBar.setTitleTextColor(
                    ContextCompat.getColor(
                        this@WebActivity,
                        it.titleTextColor
                    )
                )
                toolBar.title = it.title
                if (it.postData.isNotEmpty()) {
                    binding.webView.postUrl(it.url, it.postData.toByteArray())
                } else {
                    binding.webView.loadUrl(it.url)
                }

            }
        }

        initToolbar()
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolBar.setNavigationOnClickListener { handleBack() }
    }

    override fun initData() {

    }

    private fun initWebView() {
        binding.webView.run {
            settings.run {
                javaScriptEnabled = true
                domStorageEnabled = true
            }
            webViewClient = customWebViewClient
            webChromeClient = customWebChromeClient

            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                val nightModeFlags =
                    resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                    WebSettingsCompat.setForceDark(settings, WebSettingsCompat.FORCE_DARK_ON)
                } else {
                    WebSettingsCompat.setForceDark(settings, WebSettingsCompat.FORCE_DARK_OFF)
                }
            }
        }
    }

    private val customWebViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView,
            request: WebResourceRequest
        ): Boolean {


            if (request.url == null) return false
            try {
                if (request.url.toString().startsWith("weixin://")) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(request.url.toString()))
                    view.context.startActivity(intent)
                    return true
                }
            } catch (e: Exception) {
                return false
            }
//            view.loadUrl(request.url.toString())
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onPageFinished(view: WebView, url: String?) {
            super.onPageFinished(view, url)

            // 获取手机系统字号
            try {
                val configuration = Configuration()
                val activityManagerNative = Class.forName("android.app.ActivityManagerNative")
                val oam =
                    activityManagerNative.getMethod("getDefault").invoke(activityManagerNative)
                val config = oam.javaClass.getMethod("getConfiguration").invoke(oam)
                configuration.updateFrom(config as Configuration)
                val fontScale: Float = configuration.fontScale
                // 使用系统设置字号
                view.evaluateJavascript(
                    "document.getElementsByTagName('body')[0].style.webkitTextSizeAdjust='" + (fontScale * 100).toString() + "%'",
                    null
                )
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private val customWebChromeClient = object : WebChromeClient() {
        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            this@WebActivity.filePathCallback = filePathCallback
            val type =
                if (fileChooserParams != null && fileChooserParams.acceptTypes != null && fileChooserParams.acceptTypes.isNotEmpty()) {
                    fileChooserParams.acceptTypes[0]
                } else {
                    "image/*"
                }
            chooseLauncher.launch(type)
            return true
        }
    }

    override fun handleBack() {
        if (binding.webView.canGoBack()) {
            YLog.e(binding.webView.url ?: "")
            binding.webView.goBack()
        } else {
            super.handleBack()
        }
    }
}