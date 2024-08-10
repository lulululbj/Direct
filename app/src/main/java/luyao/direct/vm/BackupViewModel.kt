package luyao.direct.vm

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tencent.mmkv.MMKV
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import luyao.direct.DirectApp
import luyao.direct.R
import luyao.direct.model.MMKVConstants
import luyao.direct.model.dao.AppDao
import luyao.direct.model.dao.NewDirectDao
import luyao.direct.model.entity.NewBackupEntity
import luyao.direct.util.MoshiUtil
import luyao.direct.util.WebDavUtil
import luyao.ktx.ext.versionName
import luyao.ktx.util.YLog
import java.io.File
import java.io.OutputStream
import javax.inject.Inject

/**
 * author: luyao
 * date:   2021/10/14 13:42
 */
@HiltViewModel
class BackupViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var appDao: AppDao

    @Inject
    lateinit var newDirectDao: NewDirectDao

    val connectStatus = MutableLiveData<Boolean>()
    val progressLabel = MutableLiveData<String>()
    val backupResult = MutableLiveData<Result<String>>()
    val restoreResult = MutableLiveData<Result<NewBackupEntity>>()

    fun checkWebDavConnect() {
        viewModelScope.launch(Dispatchers.IO) {
            connectStatus.postValue(
                WebDavUtil.checkWebDavSetting(
                    MMKVConstants.webDavServer,
                    MMKVConstants.webDavUserName,
                    MMKVConstants.webDavPassword
                )
            )
        }
    }

    fun backupCloud() {
        progressLabel.value = DirectApp.App.getString(R.string.backing_up_to_cloud)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val appList = appDao.loadAll()
                val directList = newDirectDao.loadAll()
                val destMMKVDir = File(DirectApp.App.cacheDir, "mmkv_backup")
                if (destMMKVDir.exists()) destMMKVDir.deleteRecursively()
                MMKV.backupAllToDirectory(destMMKVDir.path)
                val mmkvFile = File(destMMKVDir, "mmkv.default")
                val mmkvCrcFile = File(destMMKVDir, "mmkv.default.crc")
                val mmkv = if (mmkvFile.exists()) mmkvFile.readBytes() else null
                val mmkvCrc = if (mmkvCrcFile.exists()) mmkvCrcFile.readBytes() else null
                val backupEntity = NewBackupEntity(
                    DirectApp.App.versionName,
                    System.currentTimeMillis(),
                    appList,
                    directList,
                    mmkv,
                    mmkvCrc
                )
                val json = MoshiUtil.newBackupEntityAdapter.toJson(backupEntity)
                val localFile = File(DirectApp.App.cacheDir, "cloud_backup.direct")
                if (localFile.exists()) localFile.delete()
                localFile.createNewFile()
                localFile.bufferedWriter().use {
                    it.write(json)
                }
                WebDavUtil.upload(localFile)
                MMKVConstants.lastBackupCloud = backupEntity.time
                backupResult.postValue(Result.success(DirectApp.App.getString(R.string.backup_success)))
            } catch (e: Exception) {
                backupResult.postValue(Result.failure(Exception(DirectApp.App.getString(R.string.backup_failed))))
            }
        }
    }


    fun checkCloudData() {
        progressLabel.value = DirectApp.App.getString(R.string.reading_cloud_backup_data)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val webDavFile = "${MMKVConstants.webDavServer}Direct/backup.direct"
                val localFile = File(DirectApp.App.cacheDir, "cloud_backup.direct")
                if (localFile.exists()) localFile.delete()
                localFile.createNewFile()
                WebDavUtil.downLoad(webDavFile, localFile)
                localFile.bufferedReader().use {
                    val backupEntity =
                        MoshiUtil.newBackupEntityAdapter.fromJson(it.readText())
                    if (backupEntity != null) {
                        restoreResult.postValue(Result.success(backupEntity))
                    } else {
                        restoreResult.postValue(Result.failure(Exception(DirectApp.App.getString(R.string.restore_failed))))
                    }
                }
            } catch (e: Exception) {
                YLog.e("restore cloud error: ${e.message}")
                restoreResult.postValue(Result.failure(Exception(DirectApp.App.getString(R.string.restore_failed))))
            }
        }
    }

    fun restoreData(entity: NewBackupEntity) {
        progressLabel.value = DirectApp.App.getString(R.string.restoring_cloud_backup_data)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                entity.directList.forEach {
                    if (it.localIcon == null) {
                        it.localIcon = ""
                    }
                    if (it.tag == null) {
                        it.tag = ""
                    }
                }
                appDao.insertAll(entity.appList)
                newDirectDao.insertAll(entity.directList)
                if (entity.mmkv != null && entity.mmkvCrc != null) {
                    val destMMKVDir = File(DirectApp.App.cacheDir, "mmkv_backup")
                    if (destMMKVDir.exists()) destMMKVDir.deleteRecursively()
                    destMMKVDir.mkdir()
                    val mmkvFile = File(destMMKVDir, "mmkv.default")
                    val mmkvCrcFile = File(destMMKVDir, "mmkv.default.crc")
                    mmkvFile.createNewFile()
                    mmkvCrcFile.createNewFile()
                    entity.mmkv.let {
                        mmkvFile.outputStream().write(it)
                    }
                    entity.mmkvCrc.let {
                        mmkvCrcFile.outputStream().write(it)
                    }
                    MMKV.restoreAllFromDirectory(destMMKVDir.path)
                }
                backupResult.postValue(Result.success(DirectApp.App.getString(R.string.restore_success)))
            } catch (e: Exception) {
                backupResult.postValue(Result.failure(Exception(DirectApp.App.getString(R.string.restore_failed))))
            }
        }
    }

    fun backupLocal() {
        progressLabel.value = DirectApp.App.getString(R.string.backing_to_local)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val appList = appDao.loadAll()
                val directList = newDirectDao.loadAll()
                val destMMKVDir = File(DirectApp.App.cacheDir, "mmkv_backup")
                if (destMMKVDir.exists()) destMMKVDir.deleteRecursively()
                MMKV.backupAllToDirectory(destMMKVDir.path)
                val mmkvFile = File(destMMKVDir, "mmkv.default")
                val mmkvCrcFile = File(destMMKVDir, "mmkv.default.crc")
                val mmkv = if (mmkvFile.exists()) mmkvFile.readBytes() else null
                val mmkvCrc = if (mmkvCrcFile.exists()) mmkvCrcFile.readBytes() else null
                val backupEntity = NewBackupEntity(
                    DirectApp.App.versionName,
                    System.currentTimeMillis(),
                    appList,
                    directList,
                    mmkv,
                    mmkvCrc
                )
                val json = MoshiUtil.newBackupEntityAdapter.toJson(backupEntity)
                val fileName = "local_backup.direct"
                var outputStream: OutputStream? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val values = ContentValues().apply {
                        put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName)
                        put(
                            MediaStore.Files.FileColumns.RELATIVE_PATH,
                            "${Environment.DIRECTORY_DOWNLOADS}/Direct"
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
                    val folder = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        "Direct"
                    )
                    if (!folder.exists())
                        folder.mkdir()
                    val file = File(folder, fileName)
                    if (file.exists()) {
                        file.delete()
                    }
                    file.createNewFile()
                    outputStream = file.outputStream()
                }
                outputStream?.use {
                    it.bufferedWriter().use { bw ->
                        bw.write(json)
                    }
                }
                MMKVConstants.lastBackupLocal = backupEntity.time
                backupResult.postValue(Result.success(DirectApp.App.getString(R.string.backup_success)))
            } catch (e: Exception) {
                backupResult.postValue(Result.failure(Exception(DirectApp.App.getString(R.string.backup_failed))))
            }
        }
    }

    fun restoreLocal(it: Uri) {
        progressLabel.value = DirectApp.App.getString(R.string.reading_local_backup_data)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                DirectApp.App.contentResolver.openInputStream(it)?.bufferedReader()?.use {
                    val backupEntity =
                        MoshiUtil.newBackupEntityAdapter.fromJson(it.readText())
                    if (backupEntity == null) {
                        restoreResult.postValue(Result.failure(Exception(DirectApp.App.getString(R.string.restore_failed))))
                    } else {
                        restoreResult.postValue(Result.success(backupEntity))
                    }
                }
            } catch (e: Exception) {
                restoreResult.postValue(Result.failure(Exception(DirectApp.App.getString(R.string.restore_failed))))
                e.printStackTrace()
            }
        }
    }

}