package luyao.sdk.extension

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 *  @author: luyao
 * @date: 2021/9/18 上午12:25
 */

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
    } catch (e:Exception){

    }
    return ""
}
