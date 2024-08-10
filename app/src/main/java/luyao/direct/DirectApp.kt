package luyao.direct

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import luyao.direct.model.MMKVConstants
import luyao.direct.util.DirectInit
import luyao.direct.worker.DirectWorker
import luyao.ktx.app.AppInit
import luyao.ktx.ext.isAfterM
import luyao.ktx.util.TimerCounter


/**
 *  @author: luyao
 * @date: 2021/7/9 上午12:19
 */
@HiltAndroidApp
class DirectApp : Application() {

    companion object {
        lateinit var App: Application
    }

    override fun onCreate() {
        super.onCreate()
        TimerCounter.start("Start1")
        TimerCounter.start("Start2")
        TimerCounter.start("DirectApp onCreate")
        App = this
        configWorker()
        AppInit.init(this)
        DirectInit.init(this)
        AppCompatDelegate.setDefaultNightMode(MMKVConstants.nightMode)
        TimerCounter.end("DirectApp onCreate")
    }

    private fun configWorker() {
        val constraints = Constraints.Builder().build()
        val directWorkRequest =
            OneTimeWorkRequestBuilder<DirectWorker>().setConstraints(constraints).build()
        WorkManager.getInstance(this).enqueue(directWorkRequest)
    }


}