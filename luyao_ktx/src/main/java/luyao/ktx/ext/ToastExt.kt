package luyao.ktx.ext

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import com.hjq.toast.Toaster

/**
 * Description:
 * Author: luyao
 * Date: 2022/7/28 11:23
 */
fun toast(message: String) {
    Toaster.show(message)
}

fun toast(@StringRes resId: Int) {
    Toaster.show(resId)
}

fun Context.snackBar(view: View, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
}

fun Context.snackBar(view: View, @StringRes resId: Int) {
    Snackbar.make(view, resId, Snackbar.LENGTH_SHORT).show()
}