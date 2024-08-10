package luyao.sdk.common

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings

/**
 *  @author: luyao
 * @date: 2021/7/15 上午12:01
 */
val Context.language
    get() =
        resources.configuration.locale.language

val Context.versionName
    get() = packageManager.getPackageInfo(packageName, 0).versionName

val Context.versionCode
    get() = packageManager.getPackageInfo(packageName, 0).versionCode

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

fun Context.openBrowser(url: String, packageName: String = "") {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    if (packageName.isNotEmpty())
        intent.setPackage(packageName)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
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
fun Context.openAssist(){
    startActivity(Intent("android.settings.VOICE_INPUT_SETTINGS"))
}