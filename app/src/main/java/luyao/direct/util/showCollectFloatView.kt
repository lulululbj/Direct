package luyao.direct.util

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Intent
import android.content.pm.PackageManager
import android.view.Gravity
import androidx.core.content.getSystemService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import luyao.direct.DirectApp
import luyao.direct.R
import luyao.direct.model.entity.AppDirect
import luyao.direct.ui.DirectActivity
import luyao.ktx.ext.dp2px
import luyao.ktx.util.YLog

const val FLOAT_COLLECT_TAG = "float_collect_tag"

fun showCollectFloatView() {
    EasyFloat.with(DirectApp.App).apply {
        setTag(FLOAT_COLLECT_TAG)
        setImmersionStatusBar(false)
        setShowPattern(ShowPattern.ALL_TIME)
//        setSidePattern(SidePattern.RIGHT)
        setLayout(R.layout.layout_float_collect) {
            it.findViewById<FloatingActionButton>(R.id.floatFab).setOnClickListener {
                DirectApp.App.getSystemService<UsageStatsManager>()?.let { usageStatsManager ->
                    val stats = usageStatsManager.queryUsageStats(
                        UsageStatsManager.INTERVAL_BEST,
                        System.currentTimeMillis() - 10 * 60 * 1000,
                        System.currentTimeMillis()
                    )
                    if (stats.isNotEmpty()) {
                        var j = 0
                        for (i in stats.indices) {
                            if (stats[i].lastTimeUsed > stats[j].lastTimeUsed) {
                                j = i
                            }
                        }
                        val packageName = stats[j].packageName
                        YLog.e(packageName)
                    }


                    val endTime = System.currentTimeMillis()
                    val usageEvents = usageStatsManager.queryEvents(
                        endTime - 10 * 1000,
                        endTime
                    )
                    var topPackageName = ""
                    var topClassName = ""
                    while (usageEvents.hasNextEvent()) {
                        val event = UsageEvents.Event()
                        usageEvents.getNextEvent(event)
                        if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                            topPackageName = event.packageName
                            topClassName = event.className
                        }
                    }
                    if (topClassName.isNotEmpty() && topPackageName.isNotEmpty()) {

                        val packageManager = DirectApp.App.packageManager
                        val packageInfo = packageManager.getPackageInfo(
                            topPackageName,
                            PackageManager.GET_ACTIVITIES
                        )
                        val exported =
                            packageInfo.activities?.filter { packageInfo -> packageInfo.name == topClassName }
                                ?.get(0)?.exported ?: false

                        val intent = Intent(DirectApp.App, DirectActivity::class.java).apply {
                            val scheme =
                                "intent:#Intent;package=${topPackageName};component=$topPackageName/${topClassName};end"
                            putExtra("collect_scheme", scheme)
                            putExtra("collect_package", topPackageName)
                            putExtra("collect_exported", exported)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        }
                        DirectApp.App.startActivity(intent)
                    }
                }
            }
        }
        setGravity(Gravity.END or Gravity.BOTTOM, -dp2px(10).toInt(), -dp2px(50).toInt())

        show()
    }
}