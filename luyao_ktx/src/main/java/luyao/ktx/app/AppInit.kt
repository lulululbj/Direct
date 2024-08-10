package luyao.ktx.app

import android.app.Application
import android.view.Gravity
import com.hjq.toast.Toaster
import com.tencent.mmkv.MMKV
import luyao.ktx.ext.dp2px
import luyao.ktx.ext.isDebuggable
import luyao.ktx.util.YLog

/**
 * Description:
 * Author: luyao
 * Date: 2022/4/22 10:05
 */
object AppInit {

    lateinit var application: Application
//    var isBackground = false

    fun init(application: Application) {
        this.application = application
        // 存在延时，慎重使用
//        ProcessLifecycleOwner.get().lifecycle.addObserver(ApplicationLifecycleObserver { isBackground ->
//            this.isBackground = isBackground
//        })
        application.registerActivityLifecycleCallbacks(KtxLifeCycleCallBack())
        MMKV.initialize(application)
        YLog.showLog = application.isDebuggable
        Toaster.init(application)
        Toaster.setGravity(Gravity.BOTTOM, 0, dp2px(40).toInt())
    }
}