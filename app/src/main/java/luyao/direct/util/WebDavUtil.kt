package luyao.direct.util

import androidx.annotation.WorkerThread
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import luyao.direct.model.MMKVConstants
import java.io.File

/**
 * author: luyao
 * date:   2021/10/14 17:15
 */
object WebDavUtil {

    private val sardine = OkHttpSardine()


    fun checkConfig() =
        MMKVConstants.webDavServer.isNotBlank() && MMKVConstants.webDavUserName.isNotBlank() && MMKVConstants.webDavPassword.isNotBlank()

    /*
     * 注意此方法需要工作在子线程
     */
    @WorkerThread
    fun checkWebDavSetting(server: String, userName: String, password: String): Boolean {

        if (server.isBlank() or userName.isBlank() or password.isBlank()) {
            return false
        }

        sardine.setCredentials(userName, password)
        try {
            sardine.list(server)
        } catch (r: Throwable) {
            return false
        }
        return true
    }

    fun upload(localFile: File) {
        val webDavDir = "${MMKVConstants.webDavServer}Direct/"
        if (!sardine.exists(webDavDir)) {
            sardine.createDirectory(webDavDir)
        }
        val webDavFile = "${webDavDir}backup.direct"
        if (sardine.exists(webDavFile)) {
            sardine.delete(webDavFile)
        }
        sardine.put(webDavFile, localFile, null)
    }

    fun downLoad(webDavFile: String, localFile: File): Result<Boolean> {
        return if (sardine.exists(webDavFile)) {
            sardine.get(webDavFile).use {
                localFile.outputStream().use { out ->
                    it.copyTo(out)
                }
            }
            Result.success(true)
        } else {
            Result.failure(Exception("文件不存在"))
        }
    }
}