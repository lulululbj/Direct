package luyao.ktx.core.event

import androidx.lifecycle.Observer

/**
 * Description:
 * Author: luyao
 * Date: 2023/8/30 14:02
 */
 class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(value: Event<T>) {
        value.getContentIfNotHandled()?.let {
            onEventUnhandledContent(it)
        }
    }
}
