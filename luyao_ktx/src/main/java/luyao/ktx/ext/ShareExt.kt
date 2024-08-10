package luyao.ktx.ext

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

fun Context.shareText(title: String, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
        putExtra(Intent.EXTRA_SUBJECT, title)
    }
    startActivity(Intent.createChooser(intent, title))
}

fun Context.shareTextToApp(title: String, text: String, specifiedPackageName: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        setPackage(specifiedPackageName)
        putExtra(Intent.EXTRA_TEXT, text)
        putExtra(Intent.EXTRA_SUBJECT, title)
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }
    startActivity(intent)
}

fun Context.shareApkFile(title: String, file: File) {
    shareFile(title, file, "application/vnd.android.package-archive")
}

fun Context.shareImageFile(title: String, file: File) {
    shareFile(title, file, "image/*")
}

fun Context.shareFile(title: String, file: File, fileType: String) {
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        type = fileType
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        putExtra(
            Intent.EXTRA_STREAM,
            FileProvider.getUriForFile(this@shareFile, "$packageName.fileprovider", file)
        )
    }
    startActivity(Intent.createChooser(intent, title))
}