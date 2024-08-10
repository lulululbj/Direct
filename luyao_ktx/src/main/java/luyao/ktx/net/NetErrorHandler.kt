package luyao.ktx.net

/**
 * Description:
 * Author: luyao
 * Date: 2022/12/15 16:25
 */
interface NetErrorHandler {
    fun onFail(errCode: Int, errorMsg: String)
    fun onError(throwable: Throwable)
}