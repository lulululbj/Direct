package luyao.direct.model.net

import luyao.direct.model.entity.DirectListEntity
import luyao.direct.model.entity.UpdateEntity
import luyao.direct.model.entity.net.CheckUpdateRequest
import luyao.ktx.model.BaseResponse
import luyao.ktx.model.BizResult
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DirectApiService {

    companion object {
//        val BASE_URL = if (BuildConfig.DEBUG) "http://192.168.31.14:8081" else "https://luyao.tech"
        val BASE_URL = "https://luyao.tech"
    }

    @GET("/direct/searchEngines/{version}")
    suspend fun loadSearchEngine(@Path("version") version: Int): BizResult<DirectListEntity>

    @POST("/direct/checkUpdate")
    suspend fun checkUpdate(@Body body: CheckUpdateRequest) : BizResult<UpdateEntity>

}