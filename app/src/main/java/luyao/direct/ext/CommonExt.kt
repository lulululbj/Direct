package luyao.direct.ext

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.pm.ShortcutManager
import android.net.Uri
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.FragmentActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.github.promeg.pinyinhelper.Pinyin
import com.permissionx.guolindev.PermissionX
import luyao.direct.DirectApp
import luyao.direct.R
import luyao.direct.model.AppIconCache
import luyao.direct.model.ExecMode
import luyao.direct.model.MMKVConstants
import luyao.direct.model.entity.NewDirectEntity
import luyao.direct.model.entity.engineList
import luyao.direct.model.entity.engineListGp
import luyao.direct.util.CommandUtil
import luyao.direct.util.customtab.CustomTabActivityHelper
import luyao.ktx.ext.*
import java.net.URLEncoder
import java.util.*


/**
 * author: luyao
 * date:   2021/7/21 20:17
 */

fun newUID() = UUID.randomUUID().toString()

fun NewDirectEntity.go(context: Context, keyword: String = "") {

    val formatKeyWord = URLEncoder.encode(keyword, "utf-8")
    try {
        if (isSearch == 1) { // 搜索引擎
            if (scheme.isNotEmpty()) { // 搜索 scheme
                val formatScheme = String.format(Locale.getDefault(), scheme, formatKeyWord)
                context.startScheme(formatScheme, this.packageName)
            } else if (!searchUrl.isNullOrEmpty()) { // 跳转网页搜索
                val url = String.format(
                    Locale.getDefault(),
                    searchUrl,
                    formatKeyWord
                )
                if (MMKVConstants.useChromeCustomTab) {
                    val customTabsIntent: CustomTabsIntent = CustomTabsIntent.Builder().build()
                    CustomTabActivityHelper.openCustomTab(
                        context, customTabsIntent, Uri.parse(url)
                    ) { _, _ ->
                        context.openBrowser(url, MMKVConstants.defaultBrowser) {
                            context.startScheme(url)
                        }
                    }
                } else {
                    context.openBrowser(
                        url,
                        this.packageName.ifEmpty { MMKVConstants.defaultBrowser }) {
                        context.startScheme(url)
                    }
                }
            } else if (packageName.isNotEmpty()) { // 分享到应用
                context.shareTextToApp(context.getString(R.string.share_to), keyword, packageName)
            }
        } else {
            if (execMode == ExecMode.SCHEME) { // scheme
                context.startScheme(scheme, this.packageName) {
                    toast(R.string.scheme_unavaliable)
                }
            } else { // root 启动
                CommandUtil.rootExec(scheme)
            }
        }
    } catch (e: Exception) {
        Toast.makeText(context, context.getString(R.string.launch_fail), Toast.LENGTH_SHORT).show()
    }
}


fun getFocusString(
    originString: String,
    keyword: String,
    @ColorInt color: Int = ContextCompat.getColor(DirectApp.App, R.color.high_light_text)
): SpannableStringBuilder {
    val start = originString.indexOf(keyword)
    val focusString = SpannableStringBuilder(originString)
    if (start < 0) return focusString
    focusString.setSpan(
        ForegroundColorSpan(color), start, start + keyword.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return focusString
}

fun Context.getChannel(): String? {
    return packageManager.getApplicationInfo(
        packageName,
        PackageManager.GET_META_DATA
    ).metaData.getString("CHANNEL")
}

// 拼音全拼
fun String.getPinyin(): String = Pinyin.toPinyin(this, "")

fun String.getExPinyin(): String {
    val builder = StringBuilder("")
    val pinyin = Pinyin.toPinyin(this, "-").split("-")
    if (pinyin.isNotEmpty()) {
        pinyin.forEach {
            if (it.isNotBlank())
                builder.append(it[0])
        }
    }
    return builder.toString()
}

fun main() {
    println("酷安".getPinyin())
    println("酷安".getExPinyin())
    println("知乎".getPinyin())
    println("知乎".getExPinyin())
    println("Google".getPinyin())
    println("Google".getExPinyin())
}

fun FragmentActivity.callPhone(number: String) {
    PermissionX.init(this)
        .permissions(Manifest.permission.CALL_PHONE)
        .onExplainRequestReason { scope, deniedList ->
            scope.showRequestReasonDialog(
                deniedList,
                getString(R.string.call_permission_note),
                getString(R.string.permission_again),
                getString(R.string.permission_no_need)
            )
        }
        .onForwardToSettings { scope, deniedList ->
            scope.showForwardToSettingsDialog(
                deniedList,
                getString(R.string.never_ask_note),
                getString(R.string.other),
                getString(R.string.suan_le)
            )
        }
        .request { allGranted, _, _ ->
            if (allGranted) {
                DirectApp.App.callPhone(number)
            }
        }
}

fun Context.getBrowserList(): List<ResolveInfo> {
    val activityIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com"))
    return packageManager.queryIntentActivities(activityIntent, PackageManager.MATCH_ALL)
}

fun Context.getProperTextColor() = MMKVConstants.panelBgColor.toTextColor(
    ContextCompat.getColor(this, R.color.black),
    ContextCompat.getColor(this, R.color.white)
)

fun TextView.setProperTextColor() {
    setTextColor(context.getProperTextColor())
}

/****************
 *
 * 发起添加群流程。群号：直达和它的用户们(472549828) 的 key 为： qdU3lao0xArgID8pYVvSPpwrHcjaIDMB
 * 调用 joinQQGroup(qdU3lao0xArgID8pYVvSPpwrHcjaIDMB) 即可发起手Q客户端申请加群 直达和它的用户们(472549828)
 *
 * @param key 由官网生成的key
 * @return 返回true表示呼起手Q成功，返回false表示呼起失败
 */
fun Context.joinQQGroup(): Boolean {
    val key = "qdU3lao0xArgID8pYVvSPpwrHcjaIDMB"
    val intent = Intent()
    intent.data =
        Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D$key")
    // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    return try {
        startActivity(intent)
        true
    } catch (e: java.lang.Exception) {
        // 未安装手Q或安装的版本不支持
        false
    }
}

fun View.makeMeasureSpec() {
    measure(
        View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    )
}

inline fun <reified T : ViewGroup.LayoutParams> View.doOnLayoutWithHeight(view: View) {
    view.makeMeasureSpec()
    doOnLayoutWithHeight<T>(view.measuredHeight)
}

inline fun <reified T : ViewGroup.LayoutParams> View.doOnLayoutWithHeight(newMeasuredHeight: Int) {
    if (layoutParams.height != measuredHeight) {
        layoutParams = (layoutParams as T).also { lp -> lp.height = newMeasuredHeight }
    }
}


fun Activity.initLicense(continueRun: () -> Unit) {
    if (!MMKVConstants.showLicenseDialog) {
        continueRun()
    } else {
//        MaterialAlertDialogBuilder(this)
//            .setTitle(R.string.license_title)
//            .setMessage(R.string.licence_content)
//            .setPositiveButton(R.string.agree) { _, _ ->
////                MMKVConstants.showLicenseDialog = false
//                continueRun()
//            }
//            .setNegativeButton(R.string.disagress) { _, _ -> finish() }
//            .setCancelable(false)
//            .show()
        MaterialDialog(this).show {
            title(R.string.license_title)
            message(R.string.licence_content) {
                html { openBrowser(it) }
            }
            positiveButton(R.string.agree) {
                MMKVConstants.showLicenseDialog = false
                continueRun()
            }
            negativeButton(R.string.disagress) { finish() }
            cancelOnTouchOutside(false)
        }
    }
}

fun NewDirectEntity.createShortcut() {
    val shortcutManager = DirectApp.App.getSystemService<ShortcutManager>() ?: return
    if (isAfterO() && shortcutManager.isRequestPinShortcutSupported) {
        val intent =
            if (scheme.startsWith("android-app")) Intent.parseUri(
                scheme,
                Intent.URI_ANDROID_APP_SCHEME
            )
            else Intent.parseUri(scheme, Intent.URI_INTENT_SCHEME)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val shortcut = ShortcutInfoCompat.Builder(DirectApp.App, label)
            .setShortLabel(label)
            .setLongLabel(label)
            .setIcon(
                IconCompat.createWithBitmap(
                    AppIconCache.get(packageName + label) ?: DirectApp.App.packageManager.run {
                        getPackageInfo(packageName, 0).applicationInfo.loadIcon(this)
                            .toBitmap(width = dp2px(36).toInt(), height = dp2px(36).toInt())
                    })
            )
            .setIntent(intent)
            .build()
        ShortcutManagerCompat.requestPinShortcut(DirectApp.App, shortcut, null)
        toast(R.string.has_create_shortcut)
    } else {
        toast(R.string.not_support_shortcut)
    }
}

fun getEngineList() =
    if (DirectApp.App.getChannel() == "official") engineList else engineListGp


