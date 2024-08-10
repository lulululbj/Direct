package luyao.direct.util

import com.topjohnwu.superuser.Shell
import luyao.direct.DirectApp
import luyao.direct.R
import luyao.ktx.ext.toast

object CommandUtil {

    fun rootExec(scheme: String) {
        if (Shell.isAppGrantedRoot() == true) {
            rootExecScheme(scheme)
        } else {
            if (ShellManager.acquireRoot()) {
                rootExecScheme(scheme)
            } else {
                toast(R.string.no_root)
            }
        }
    }

    private fun rootExecScheme(scheme: String) {
        ShellManager.exec(getCommandFromScheme(scheme))
    }

    private fun getCommandFromScheme(scheme: String): String {
        if (scheme.startsWith("intent:#")) {
            var packageName = ""
            var className = ""
            scheme.split(";").forEach {
                if (it.startsWith("package")) {
                    packageName = it.substring(8)
                }
                if (it.startsWith("component")) {
                    val spits = it.split("/")
                    if (spits.size > 1) {
                        className = spits[1]
                        if (packageName.isEmpty()) {
                            packageName = spits[0].substring(10)
                        }
                    } else {
                        className = spits[0].substring(10)
                    }
                }
            }
            return "am start -n $packageName/$className"
        } else {
            return "am start -d $scheme"
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val scheme =
            "intent:#Intent;package=com.alibaba.android.rimet;component=com.alibaba.android.rimet/com.alibaba.android.rimet.biz.LaunchHomeActivity;end"

        println(getCommandFromScheme(scheme))
    }

}