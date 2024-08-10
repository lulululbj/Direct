package luyao.direct.view

import android.app.Activity
import luyao.direct.R
import luyao.direct.ui.engine.EngineManageActivity
import luyao.ktx.ext.showConfirmDialog
import luyao.ktx.ext.startActivity

fun Activity.showGuideNewEngineDialog() {
    showConfirmDialog(
        title = getString(R.string.haowai),
        message = "更多的内置搜索引擎，\n更快捷的管理方式,\n快来体验吧！",
        confirmText = R.string.go_see,
        cancelText = R.string.no_need
    ) {
        startActivity<EngineManageActivity>()
    }
}