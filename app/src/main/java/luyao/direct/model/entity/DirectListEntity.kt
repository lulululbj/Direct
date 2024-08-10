package luyao.direct.model.entity

import com.squareup.moshi.JsonClass

/**
 * author: luyao
 * date:   2021/9/27 11:33
 */
@JsonClass(generateAdapter = true)
data class DirectListEntity(val version: Int, val schemes: List<NewDirectEntity>)