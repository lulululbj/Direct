package luyao.direct.model.net

import luyao.ktx.net.BaseRetrofitClient
import okhttp3.OkHttpClient

object DirectRetrofitClient : BaseRetrofitClient() {

    val service by lazy { getService<DirectApiService>(DirectApiService.BASE_URL) }

    override fun handlerOkHttpClientBuilder(builder: OkHttpClient.Builder) {

    }
}