package com.lyy.impl

import android.animation.Animator
import android.text.Editable
import android.text.TextWatcher
import android.view.animation.Animation

/**
 *
 *
 * @author liangxiaxu@leyaoyao.com
 * @date 2021-10-26
 */
class SimpleAnimationListener : Animation.AnimationListener {
    override fun onAnimationStart(animation: Animation) {}
    override fun onAnimationEnd(animation: Animation) {}
    override fun onAnimationRepeat(animation: Animation) {}
}

open class SimpleAnimatorListener : Animator.AnimatorListener {
    override fun onAnimationStart(animator: Animator) {}
    override fun onAnimationEnd(animator: Animator) {}
    override fun onAnimationCancel(animator: Animator) {}
    override fun onAnimationRepeat(animator: Animator) {}
}

open class SimpleTextWatcher : TextWatcher {

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {

    }
}

