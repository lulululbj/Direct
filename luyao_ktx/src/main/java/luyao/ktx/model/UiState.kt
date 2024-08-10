package luyao.ktx.model

/**
 * Description:
 * Author: luyao
 * Date: 2023/8/29 10:06
 */
sealed interface UiState<out T> {
     object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val errMsg: String) : UiState<Nothing>
}
