package luyao.direct.util

import luyao.direct.model.entity.BackupEntity
import luyao.ktx.ext.aesDecrypt
import luyao.ktx.ext.aesEncrypt
import luyao.ktx.ext.initAESKey
import java.io.File
import java.io.InputStream

/**
 * author: luyao
 * date:   2021/10/15 10:38
 */

/*
 * 文件格式为 [密钥][IV][BackupEntity JSON]，时间戳和备份内容整体加密
 */
fun backUpEncrypt(content: String): ByteArray {
    val key = initAESKey(256)
    val iv = initAESKey(128)
    val encryptContent = content.toByteArray().aesEncrypt(key, iv)
    return key + iv + encryptContent
}

fun File.backUpDecrypt() =
    inputStream().backUpDecrypt()


fun InputStream.backUpDecrypt(): BackupEntity? {
    use {
        val keyBytes = ByteArray(32)
        val ivBytes = ByteArray(16)
        val encryptBytes = ByteArray(it.available() - 48)
        it.read(keyBytes)
        it.read(ivBytes)
        it.read(encryptBytes)
        val contentBytes = encryptBytes.aesDecrypt(keyBytes, ivBytes)
        return MoshiUtil.backupEntityAdapter.fromJson(String(contentBytes))
    }
}