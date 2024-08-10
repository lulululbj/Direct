package luyao.ktx.ext

import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Description:
 * Author: luyao
 * Date: 2023/5/24 16:50
 */
fun View.handleInset(action: (View, Insets) -> Unit) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        action(view, insets)
        windowInsets
    }
}