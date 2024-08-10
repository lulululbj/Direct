package luyao.ktx.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import luyao.ktx.model.BizResult

/**
 * Description:
 * Author: luyao
 * Date: 2023/3/30 11:14
 */
open class BaseViewModel : ViewModel() {

    fun <T> flowNet(
        flow: Flow<BizResult<T>>,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (T) -> Unit = {}
    ) {
        flow
            .flowOn(Dispatchers.IO) // flowOn 仅影响上游
            .onEach {
                if (it is BizResult.Success && it.data != null) onSuccess.invoke(it.data)
                else if (it is BizResult.Error) {
                    onError.invoke(it.exception ?: Throwable("未知异常"))
                }
            }
            .onCompletion {

            }
            .catch {
                onError.invoke(it)
            }
            .launchIn(viewModelScope)
    }

}