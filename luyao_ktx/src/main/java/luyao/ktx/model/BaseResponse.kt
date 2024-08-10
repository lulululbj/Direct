package luyao.ktx.model

const val CODE_SUCCESS = 0
const val CODE_ERROR = -1


data class BaseResponse<out T>(val errorCode: Int, val errorMsg: String, val data: T)

fun <T : Any> BaseResponse<T>.handleResponse(
    successBlock: ((T) -> Unit)? = null,
    errorBlock: ((String) -> Unit)? = null,
): BaseResponse<T> {
    if (errorCode == CODE_SUCCESS) {
        successBlock?.invoke(data)
    } else {
        errorBlock?.invoke(errorMsg)
    }
    return this
}

sealed class ApiResponse<T> {
    data class Success<T>(val data: T)
    data class Fail<T>(val errorCode: Int, val errorMsg: String) : ApiResponse<T>()
    data class Error<T>(val throwable: Throwable) : ApiResponse<T>()
}