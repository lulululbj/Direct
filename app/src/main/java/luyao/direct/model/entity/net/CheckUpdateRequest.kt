package luyao.direct.model.entity.net

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CheckUpdateRequest(
    val versionCode: Long
)