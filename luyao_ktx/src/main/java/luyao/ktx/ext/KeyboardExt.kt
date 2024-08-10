package luyao.ktx.ext

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 *  @author: luyao
 * @date: 2021/7/8 下午11:40
 */

fun Context.showKeyboard(view: View) {
    val inputMethodService = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    view.isFocusable = true
    view.isFocusableInTouchMode = true
    view.requestFocus()
    inputMethodService.showSoftInput(view,0)
    inputMethodService.toggleSoftInput(
        InputMethodManager.SHOW_FORCED,
        InputMethodManager.HIDE_IMPLICIT_ONLY
    )
}

fun Context.hideKeyboard(view: View) {
    val inputMethodService = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodService.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
//    inputMethodService.toggleSoftInput(0,0)
}