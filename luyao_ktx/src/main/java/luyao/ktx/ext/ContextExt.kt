package luyao.ktx.ext

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.google.android.material.snackbar.Snackbar
import luyao.ktx.util.YLog

/**
 * Description:
 * Author: luyao
 * Date: 2022/7/29 11:09
 */

val Context.language
    get() =
        resources.configuration.locale.language

val Context.isDebuggable: Boolean
    get() = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

val Context.versionName: String
    get() = packageManager.getPackageInfo(packageName, 0).versionName

val Context.versionCode: Long
    get() = if (isBeforeP()) {
        packageManager.getPackageInfo(packageName, 0).versionCode.toLong()
    } else {
        packageManager.getPackageInfo(packageName, 0).longVersionCode
    }

fun Context.isAppInstalled(packageName: String): Boolean {
    if (packageName.isEmpty()) return false
    return try {
        packageManager.getApplicationInfo(packageName, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

fun Context.isAppEnabled(packageName: String): Boolean {
    if (packageName.isEmpty()) return false
    return try {
        return packageManager.getApplicationInfo(packageName, 0).enabled
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

fun Context.canLaunch(packageName: String): Boolean {
    return packageManager.getLaunchIntentForPackage(packageName) != null
}

fun Context.openApp(packageName: String): Boolean {
    val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
    return if (launchIntent == null) {
        false
    } else {
        startActivity(launchIntent)
        true
    }
}

fun Context.openAppSetting(packageName: String) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }
    startActivity(intent)
}

fun Context.callPhone(phoneNumber: String) {
    val intent = Intent().apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        action = Intent.ACTION_CALL
        data = Uri.parse("tel:$phoneNumber")
    }
    startActivity(intent)
}

fun Context.sendSms(phoneNumber: String) {
    val intent = Intent().apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        action = Intent.ACTION_VIEW
        data = Uri.parse("smsto:$phoneNumber")
    }
    startActivity(intent)
}

fun Context.sendEmail(emailAddress: String, onError: (() -> Unit)? = null) {
    try {
        val intent = Intent().apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            action = Intent.ACTION_SENDTO
            data = Uri.parse("mailto:$emailAddress")
        }
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        onError?.invoke()
    }

}

fun Context.openBrowser(url: String, packageName: String = "", onError: (() -> Unit)? = null) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (packageName.isNotEmpty())
            intent.setPackage(packageName)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    } catch (e: Exception) {
        YLog.e("open browser error: ${e.message}")
        onError?.invoke()
    }
}

fun Context.startScheme(
    realScheme: String,
    packageName: String = "",
    onError: (() -> Unit)? = null
) {
    try {
        // 快捷方式
        val intent =
            if (realScheme.startsWith("android-app")) Intent.parseUri(
                realScheme,
                Intent.URI_ANDROID_APP_SCHEME
            )
            else Intent.parseUri(realScheme, Intent.URI_INTENT_SCHEME)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (packageName.isNotEmpty()) {
            intent.setPackage(packageName)
        }
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        onError?.invoke()
    } catch (e: SecurityException) {
        e.printStackTrace()
        onError?.invoke()
    }
}

fun Context.restartApp() {
    packageManager.getLaunchIntentForPackage(packageName)?.let {
        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(it)
    }
}

/**
 * 跳转到数字助理页面
 */
fun Context.openAssist(openError: ((String) -> Unit)? = null) {
    try {
        startActivity(Intent("android.settings.VOICE_INPUT_SETTINGS"))
    } catch (e: Exception) {
        openError?.invoke(e.message ?: "")
        e.printStackTrace()
    }
}

/**
 * 跳转应用市场指定应用
 * [packageName] 应用包名，默认当前应用
 * [browserUrl] 未安装到应用市场时，引导到网页
 */
@SuppressLint("QueryPermissionsNeeded")
fun Context.jumpAppStore(
    packageName: String = this.packageName,
    storePackageName: String = "",
    browserUrl: String = ""
) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("market://details?id=$packageName")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (storePackageName.isNotEmpty()) {
            setPackage(storePackageName)
        }
    }
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else if (browserUrl.isNotEmpty()) {
        openBrowser(browserUrl)
    }
}

fun Context.copyToClipBoard(text: String, label: String = "label") {
    val manager = getSystemService<ClipboardManager>()
    val clipData = ClipData.newPlainText(label, text)
    manager?.setPrimaryClip(clipData)
}

fun View.snackBar(text: String, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, text, duration).show()
}

fun Context.hideTask(needExclude: Boolean = true) {
    (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).appTasks.run {
        if (isNotEmpty()) {
            get(0).setExcludeFromRecents(needExclude)
        }
    }
}

fun Context.isGranted(permission: String): Boolean {
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

fun Context.getAppName(packageName: String): String {

    try {
        return packageManager.getApplicationLabel(
            packageManager.getApplicationInfo(
                packageName,
                0
            )
        ) as String
    } catch (e: Exception) {

    }
    return ""
}
