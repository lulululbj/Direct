package luyao.ktx.ext

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.animation.Animation
import androidx.core.view.isVisible
import androidx.transition.Transition

/**
 * Description:
 * Author: luyao
 * Date: 2022/8/3 19:33
 */

fun Animation.listen(
    onStart: (() -> Unit)? = null,
    onEnd: (() -> Unit)? = null,
    onRepeat: (() -> Unit)? = null,
) {
    setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {
            onStart?.invoke()
        }

        override fun onAnimationEnd(animation: Animation?) {
            onEnd?.invoke()
        }

        override fun onAnimationRepeat(animation: Animation?) {
            onRepeat?.invoke()
        }

    })
}

fun Transition.listener(
    doOnStart: ((Transition) -> Unit)? = null,
    doOnEnd: ((Transition) -> Unit)? = null
) {
    addListener(object : Transition.TransitionListener {
        override fun onTransitionStart(transition: Transition) {
            doOnStart?.invoke(transition)
        }

        override fun onTransitionEnd(transition: Transition) {
            doOnEnd?.invoke(transition)
        }

        override fun onTransitionCancel(transition: Transition) {
        }

        override fun onTransitionPause(transition: Transition) {
        }

        override fun onTransitionResume(transition: Transition) {
        }

    })
}

fun View.alphaOut(duration: Long = 300) {
    animate().alpha(0f)
        .setDuration(duration).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                isVisible = false
            }
        }).start()
}

fun View.alphaIn(duration: Long = 300) {
    animate().alpha(1f)
        .setDuration(300).setListener(object : AnimatorListenerAdapter() {

            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                isVisible = true
            }
        }).start()
}