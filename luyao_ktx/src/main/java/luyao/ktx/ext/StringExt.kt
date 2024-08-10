package luyao.ktx.ext

import android.util.Base64

/**
 * author: luyao
 * date:   2021/10/29 16:57
 */

fun String.base64() = Base64.encodeToString(toByteArray(), Base64.DEFAULT)
fun String.deCodeBase64() = Base64.decode(this, Base64.DEFAULT)