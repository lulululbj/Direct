package luyao.ktx.util

import android.util.Log

/**
 * Description:
 * Author: luyao
 * Date: 2022/8/3 10:15
 */
object YLog {

    var tag = "YLog"
    var showLog = true

    fun v(msg: String, tag: String = this.tag) {
        if (showLog) {
            Log.v(tag, msg)
        }
    }

    fun d(msg: String, tag: String = this.tag) {
        if (showLog) {
            Log.d(tag, msg)
        }
    }

    fun i(msg: String, tag: String = this.tag) {
        if (showLog) {
            Log.i(tag, msg)
        }
    }

    fun w(msg: String, tag: String = this.tag) {
        if (showLog) {
            Log.w(tag, msg)
        }
    }

    fun e(msg: String, tag: String = this.tag) {
        if (showLog) {
            Log.e(tag, msg)
        }
    }
}