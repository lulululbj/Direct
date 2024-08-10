package luyao.ktx.ext

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.getSystemService

fun Context.copyToClipboard(text: String) {
    val service = getSystemService<ClipboardManager>()
    service?.setPrimaryClip(ClipData.newPlainText("text", text))
}

fun Context.getClipboardText(): String {
    val clipboard = getSystemService<ClipboardManager>() ?: return ""
    if (!clipboard.hasPrimaryClip()) return ""
    return clipboard.primaryClip?.getItemAt(0)?.text.toString()
}