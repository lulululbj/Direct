package luyao.ktx.coroutine

import kotlinx.coroutines.CoroutineExceptionHandler
import luyao.ktx.util.YLog

/**
 * Description:
 * Author: luyao
 * Date: 2022/10/11 22:59
 */
val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
    YLog.e("coroutineExceptionHandler: ${throwable.message}")
}