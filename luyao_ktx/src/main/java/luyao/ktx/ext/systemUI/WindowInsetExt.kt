package luyao.ktx.ext.systemUI

import android.view.View
import luyao.ktx.ext.handleInset

/**
 * Description:
 * Author: luyao
 * Date: 2023/10/10 13:21
 */
fun View.handleRootInset() {
    handleInset { view, insets ->
        view.setPadding(insets.left, insets.top, insets.right, 0)
    }
}