package luyao.ktx.ext

import android.view.inputmethod.EditorInfo
import android.widget.EditText

/**
 * Description:
 * Author: luyao
 * Date: 2022/10/4 22:34
 */

fun EditText.onActionSearch(action: (String) -> Unit) {
    setOnEditorActionListener { v, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            action(v.text.toString())
        }
        false
    }
}