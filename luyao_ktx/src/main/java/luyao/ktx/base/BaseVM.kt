package luyao.ktx.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import luyao.ktx.core.event.Event
import luyao.ktx.model.BizResult
import luyao.ktx.model.UiState

/**
 * Description:
 * Author: luyao
 * Date: 2023/8/30 13:48
 */
open class BaseVM : ViewModel() {

    // 通用的 Loading，成功，失败状态
    val uiStateEvent = MutableLiveData<Event<UiState<Any>>>()

    // 通用的返回事件
    val backEvent = MutableLiveData<Event<Boolean>>()

    fun <T> handleBusiness(
        flow: Flow<T>,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (T) -> Unit = {},
    ) {
        flow.flowOn(Dispatchers.IO)
            .flowOn(Dispatchers.Main)
            .onEach {
                onSuccess.invoke(it)
            }.onCompletion {

            }.catch {
                uiStateEvent.value = Event(UiState.Error(it.message ?: "未知异常"))
                onError.invoke(it)
            }.launchIn(viewModelScope)
    }

    fun <T> handleNet(
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