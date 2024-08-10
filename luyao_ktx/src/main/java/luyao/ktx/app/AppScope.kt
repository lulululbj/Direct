package luyao.ktx.app

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import luyao.ktx.coroutine.coroutineExceptionHandler

/**
 * Description:
 * Author: luyao
 * Date: 2022/12/13 13:24
 */
object AppScope {

    val appScope = CoroutineScope( SupervisorJob() +  coroutineExceptionHandler)

    fun launchIO(block: CoroutineScope.() -> Unit) {
        appScope.launch(Dispatchers.IO) { block.invoke(this) }
    }

}