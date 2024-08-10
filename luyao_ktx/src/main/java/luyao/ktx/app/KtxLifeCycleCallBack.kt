package luyao.ktx.app

import android.app.Activity
import android.app.Application
import android.os.Bundle

class KtxLifeCycleCallBack : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        KtxActivityManager.pushActivity(activity)
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
        KtxActivityManager.pushActivity(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        KtxActivityManager.popActivity(activity)
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }


}