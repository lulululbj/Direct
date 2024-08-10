package luyao.direct.vm

import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import luyao.direct.DirectApp
import luyao.direct.R
import luyao.direct.model.entity.AppEntity
import luyao.ktx.coroutine.coroutineExceptionHandler
import luyao.ktx.ext.toast
import java.io.File
import java.io.OutputStream
import java.util.*

class AppViewModel : ViewModel() {

    val shareApkFileData = MutableLiveData<File>()
    val requestPermissionData = MutableLiveData<Pair<AppEntity, Boolean>>()

    fun shareApk(appEntity: AppEntity) {
        val fileName =
            "${appEntity.appName}_${appEntity.versionName}(${appEntity.versionCode}).apk"
        val folder = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "Direct/apk"
        )
        val apkFile = File(folder, fileName)
        if (apkFile.exists()) {
            shareApkFileData.value = apkFile
        } else {
            backupApk(appEntity, true)
        }
    }


    fun backupApk(appEntity: AppEntity, needShare: Boolean = false) {
        val fileName =
            "${appEntity.appName}_${appEntity.versionName}(${appEntity.versionCode}).apk"
        val folder = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "Direct/apk"
        )
        val apkFile = File(folder, fileName)
        if (apkFile.exists()) {
            if (needShare) {
                shareApkFileData.value = apkFile
//                activity.shareApk("Share", apkFile)
            } else {
                toast(
                    String.format(
                        Locale.getDefault(),
                        DirectApp.App.getString(R.string.backup_apk_success),
                        folder.path
                    )
                )
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                backupApkFile(appEntity, needShare)
            } else {
                requestPermissionData.value = appEntity to needShare
            }
        }
    }

    fun backupApkFile(
        appEntity: AppEntity,
        needShare: Boolean = false
    ) {

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val folder = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "Direct/apk"
            )
            val originApkFile = DirectApp.App.packageManager.getPackageInfo(
                appEntity.packageName,
                0
            ).applicationInfo.sourceDir
            val fileName =
                "${appEntity.appName}_${appEntity.versionName}(${appEntity.versionCode}).apk"
            var outputStream: OutputStream? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName)
                    put(
                        MediaStore.Files.FileColumns.RELATIVE_PATH,
                        "${Environment.DIRECTORY_DOWNLOADS}/Direct/apk"
                    )
                }
                val uri = DirectApp.App.contentResolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    values
                )
                uri?.let {
                    outputStream = DirectApp.App.contentResolver.openOutputStream(it)
                }
            } else {

                if (!folder.exists())
                    folder.mkdirs()
                val file = File(folder, fileName)
                if (file.exists()) {
                    file.delete()
                }
                file.createNewFile()
                outputStream = file.outputStream()
            }
            File(originApkFile).inputStream().buffered().use { bfis ->
                outputStream?.use { outputStream ->
                    bfis.copyTo(outputStream)
                }
            }
            val apkFile = File(folder, fileName)
            if (needShare) {
                shareApkFileData.postValue(apkFile)
//                activity.shareApk(activity.getString(R.string.share_apk), apkFile)
            } else {
                toast(
                    String.format(
                        Locale.getDefault(),
                        DirectApp.App.getString(R.string.backup_apk_success),
                        folder.path
                    )
                )
            }
        }
    }
}