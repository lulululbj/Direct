package luyao.ktx.ext

import android.app.Activity
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import luyao.ktx.R

fun Activity.showConfirmDialog(
    title: String = getString(R.string.Note),
    message: String,
    @StringRes confirmText: Int = R.string.Confirm,
    @StringRes cancelText: Int = R.string.Cancel,
    onCancel: (() -> Unit)? = null,
    onConfirm: () -> Unit,
) {
    MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(confirmText) { _, _ ->
            onConfirm()
        }
        .setNegativeButton(cancelText) { _, _ ->
            onCancel?.invoke()
        }
        .show()
//    MaterialDialog(this).show {
//        title(text = title)
//        message(text = message)
//        positiveButton(confirmText) {
//            onConfirm()
//        }
//        negativeButton(cancelText) {
//            onCancel?.invoke()
//        }
//    }
}

fun Activity.showNoteDialog(@StringRes resId: Int) {
    MaterialAlertDialogBuilder(this)
        .setMessage(resId)
        .setPositiveButton(R.string.Confirm) { _, _ -> }
        .show()
//    MaterialDialog(this).show {
//        message(resId)
//        positiveButton(R.string.Confirm)
//    }
}

fun Activity.showNoteDialog(message: String) {
    MaterialAlertDialogBuilder(this)
        .setMessage(message)
        .setPositiveButton(R.string.Confirm) { _, _ -> }
        .show()
//    MaterialDialog(this).show {
//        message(text = message)
//        positiveButton(R.string.Confirm)
//    }
}

fun Fragment.showConfirmDialog(
    title: String = getString(R.string.Note),
    message: String,
    @StringRes confirmText: Int = R.string.Confirm,
    @StringRes cancelText: Int = R.string.Cancel,
    onCancel: (() -> Unit)? = null,
    onConfirm: () -> Unit,
) {
    MaterialAlertDialogBuilder(requireActivity())
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(confirmText) { _, _ ->
            onConfirm()
        }
        .setNegativeButton(cancelText) { _, _ ->
            onCancel?.invoke()
        }
        .show()
//    MaterialDialog(requireContext()).show {
//        title(text = title)
//        message(text = message)
//        positiveButton(confirmText) {
//            onConfirm()
//        }
//        negativeButton(cancelText) {
//            onCancel?.invoke()
//        }
//}
}

fun Fragment.showNoteDialog(message: String) {
    MaterialAlertDialogBuilder(requireActivity())
        .setMessage(message)
        .setPositiveButton(R.string.Confirm) { _, _ -> }
        .show()
//    MaterialDialog(requireActivity()).show {
//        message(text = message)
//        positiveButton(R.string.Confirm)
//    }
}