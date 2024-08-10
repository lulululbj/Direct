package luyao.direct.view.windowinset

import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat

class InnerTranslateDeferringInsetsAnimationCallback(
    private val view: View?,
    val persistentInsetTypes: Int,
    val deferredInsetTypes: Int,
    dispatchMode: Int = DISPATCH_MODE_STOP,
    val onProgressListener: ((Int) -> Unit)? = null,
    val onEndListener: (() -> Unit)? = null
) : WindowInsetsAnimationCompat.Callback(dispatchMode) {
    init {
        require(persistentInsetTypes and deferredInsetTypes == 0) {
            "persistentInsetTypes and deferredInsetTypes can not contain any of " +
                    " same WindowInsetsCompat.Type values"
        }
    }

    override fun onProgress(
        insets: WindowInsetsCompat,
        runningAnimations: List<WindowInsetsAnimationCompat>
    ): WindowInsetsCompat {
        // onProgress() is called when any of the running animations progress...
//            if (binding.translateLayout.visibility == View.GONE) {
//                binding.translateLayout.visibility = View.VISIBLE
//            }
        // First we get the insets which are potentially deferred
        val typesInset = insets.getInsets(deferredInsetTypes)
        // Then we get the persistent inset types which are applied as padding during layout
        val otherInset = insets.getInsets(persistentInsetTypes)

        // Now that we subtract the two insets, to calculate the difference. We also coerce
        // the insets to be >= 0, to make sure we don't use negative insets.
        val diff = Insets.subtract(typesInset, otherInset).let {
            Insets.max(it, Insets.NONE)
        }

        // The resulting `diff` insets contain the values for us to apply as a translation
        // to the view
        view?.translationX = (diff.left - diff.right).toFloat()
        view?.translationY = (diff.top - diff.bottom).toFloat()

        val keyBoardHeight = diff.bottom - diff.top + insets.getInsets(persistentInsetTypes).bottom
        onProgressListener?.invoke(keyBoardHeight)
        return insets
    }

    override fun onEnd(animation: WindowInsetsAnimationCompat) {
        // Once the animation has ended, reset the translation values
        view?.translationX = 0f
        view?.translationY = 0f

        if (animation.typeMask and WindowInsetsCompat.Type.ime() != 0) {
            onEndListener?.invoke()
        }
    }
}