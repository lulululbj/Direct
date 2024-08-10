package luyao.direct.model.entity

import com.squareup.moshi.JsonClass

/**
 *  @author: luyao
 * @date: 2021/10/7 上午6:56
 */
@JsonClass(generateAdapter = true)
data class UpdateEntity(
    val versionCode: Long,
    val versionName: String,
    val force: Boolean,
    val apkSize: Long,
    val md5: String,
    val apkUrl: String,
    val desc: String
)

data class UpdateCheckEntity(
    val needUpdate: Boolean,
    val updateEntity: UpdateEntity?
)