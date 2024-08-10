package luyao.direct.util

import com.topjohnwu.superuser.Shell
import luyao.direct.BuildConfig

object ShellManager {

    init {
        Shell.enableVerboseLogging = BuildConfig.DEBUG
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(10)
        )
    }

    fun exec(command: String): String {
        return Shell.cmd(command).exec().out.toString()
    }

    fun acquireRoot(): Boolean {
        return Shell.cmd("su").exec().isSuccess
    }
}