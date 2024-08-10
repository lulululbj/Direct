package luyao.direct.model.entity

import com.squareup.moshi.JsonClass

/**
 * author: luyao
 * date:   2021/10/15 11:08
 */
@JsonClass(generateAdapter = true)
data class NewBackupEntity(
    val appVersion: String,
    val time: Long,
    val appList: List<AppEntity>,
    val directList: List<NewDirectEntity>,
    val mmkv: ByteArray? = null,
    val mmkvCrc: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NewBackupEntity

        if (appVersion != other.appVersion) return false
        if (time != other.time) return false
        if (appList != other.appList) return false
        if (directList != other.directList) return false
        if (!mmkv.contentEquals(other.mmkv)) return false
        if (!mmkvCrc.contentEquals(other.mmkvCrc)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = appVersion.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + appList.hashCode()
        result = 31 * result + directList.hashCode()
        result = 31 * result + mmkv.contentHashCode()
        result = 31 * result + mmkvCrc.contentHashCode()
        return result
    }
}