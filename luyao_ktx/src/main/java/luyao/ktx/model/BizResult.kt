package luyao.ktx.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed interface BizResult<out R> {
    data class Success<out T>(val data: T, val message: String = "") : BizResult<T>
    data class Error(val exception: Throwable? = null) : BizResult<Nothing>
    data class Loading(val time: Long = System.currentTimeMillis()) : BizResult<Nothing>

    val isSuccess: Boolean
        get() = this is Success
}

fun <R> BizResult<R>.onSuccess(action: (R) -> Unit) {
    if (this is BizResult.Success) {
        action(data)
    }
}

fun <T> Flow<T>.asResult(): Flow<BizResult<T>> {
    return this
        .map<T, BizResult<T>> {
            BizResult.Success(it)
        }
        .onStart { emit(BizResult.Loading()) }
        .catch { emit(BizResult.Error(it)) }
}