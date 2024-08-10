package luyao.ktx.app

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import luyao.ktx.util.YLog

class ApplicationLifecycleObserver(private val backgroundChange: ((Boolean) -> Unit)? = null) :
    DefaultLifecycleObserver {

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        YLog.d("Application onCreate")
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        YLog.d("Application onStart")
        backgroundChange?.invoke(false)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        YLog.d("Application onResume")
        backgroundChange?.invoke(false)
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        YLog.d("Application onPause")
        backgroundChange?.invoke(true)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        YLog.d("Application onStop")
        backgroundChange?.invoke(true)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        YLog.d("Application onDestroy")
    }

}