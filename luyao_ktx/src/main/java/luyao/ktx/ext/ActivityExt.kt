package luyao.ktx.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import java.io.Serializable

/**
 * Description:
 * Author: luyao
 * Date: 2022/7/30 22:55
 */
inline fun <reified T : Activity> Activity.startActivity(
    vararg values: Pair<String, Any>,
    flag: Int? = null,
    extra: Bundle? = null
) =
    startActivity(getIntent<T>(flag, extra, *values))


inline fun <reified T : Activity> Fragment.startActivity(
    vararg values: Pair<String, Any>,
    flag: Int? = null,
    extra: Bundle? = null,
) =
    activity?.let {
        startActivity(it.getIntent<T>(flag, extra, *values))
    }

inline fun <reified T : Activity> Context.startActivity(
    vararg values: Pair<String, Any>,
    flag: Int? = null,
    extra: Bundle? = null,
) =
    startActivity(getIntent<T>(flag, extra, *values))


inline fun <reified T : Activity> Activity.startActivityForResult(
    vararg values: Pair<String, Any>,
    requestCode: Int,
    flag: Int? = null,
    extra: Bundle? = null,
) =
    startActivityForResult(getIntent<T>(flag, extra, *values), requestCode)


inline fun <reified T : Activity> Fragment.startActivityForResult(
    requestCode: Int,
    flag: Int? = null,
    extra: Bundle? = null,
    vararg values: Pair<String, Any>
) =
    activity?.let {
        startActivityForResult(it.getIntent<T>(flag, extra, *values), requestCode)
    }

inline fun <reified T : Context> Context.getIntent(
    flag: Int? = null,
    extra: Bundle? = null,
    vararg pairs: Pair<String, Any>
): Intent =
    Intent(this, T::class.java).apply {
        flag?.let { addFlags(flag) }
        extra?.let { putExtras(extra) }
        pairs.forEach { pair ->
            val name = pair.first
            when (val value = pair.second) {
                is Int -> putExtra(name, value)
                is Byte -> putExtra(name, value)
                is Char -> putExtra(name, value)
                is Short -> putExtra(name, value)
                is Boolean -> putExtra(name, value)
                is Long -> putExtra(name, value)
                is Float -> putExtra(name, value)
                is Double -> putExtra(name, value)
                is String -> putExtra(name, value)
                is CharSequence -> putExtra(name, value)
                is Parcelable -> putExtra(name, value)
                is Array<*> -> putExtra(name, value)
                is ArrayList<*> -> putExtra(name, value)
                is Serializable -> putExtra(name, value)
                is BooleanArray -> putExtra(name, value)
                is ByteArray -> putExtra(name, value)
                is ShortArray -> putExtra(name, value)
                is CharArray -> putExtra(name, value)
                is IntArray -> putExtra(name, value)
                is LongArray -> putExtra(name, value)
                is FloatArray -> putExtra(name, value)
                is DoubleArray -> putExtra(name, value)
                is Bundle -> putExtra(name, value)
                is Intent -> putExtra(name, value)
            }
        }
    }

