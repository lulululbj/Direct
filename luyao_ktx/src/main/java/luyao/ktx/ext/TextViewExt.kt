package luyao.ktx.ext

import android.widget.TextView

/**
 * Description:
 * Author: luyao
 * Date: 2022/5/17 16:05
 */

fun TextView.hasContent() = !text.isNullOrEmpty()