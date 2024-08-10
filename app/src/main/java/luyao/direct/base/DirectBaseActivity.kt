package luyao.direct.base

import android.graphics.Color
import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import luyao.ktx.base.BaseActivity
import luyao.ktx.ext.isBeforeR
import luyao.ktx.ext.systemUI.immersiveStatusBar


/**
 * Description:
 * Author: luyao
 * Date: 2022/10/4 22:20
 */
abstract class DirectBaseActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isBeforeR()) {
            immersiveStatusBar()
//            immersiveNavigationBar()
        } else {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.statusBarColor = Color.TRANSPARENT
        }

    }
}