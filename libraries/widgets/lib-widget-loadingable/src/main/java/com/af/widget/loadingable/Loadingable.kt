package com.lyy.ev.operation.widget.loading

import android.content.Context
import android.view.View

/**
 * =================================================================================================
 *     __
 *    / /  ___    __  __  ____ _  ____    __  __  ____ _  ____
 *   / /  / _ \  / / / / / __ `/ / __ \  / / / / / __ `/ / __ \
 *  / /  /  __/ / /_/ / / /_/ / / /_/ / / /_/ / / /_/ / / /_/ /
 * /_/   \___/  \__, /  \__,_/  \____/  \__, /  \__,_/  \____/
 *             /____/                  /____/
 * =================================================================================================
 *
 * 可显示加载动画的
 *
 * @author liangxiaxu@leyaoyao.com
 * @date 2022-04-25
 */
interface Loadingable {

    fun isLoading(): Boolean

    fun showLoading()

    fun hideLoading()

    fun setProgressViewEndTarget(scale: Boolean, end: Int)

    // ---------------------------------------------------------------------------------------------

    fun getContext(): Context

    fun setEnabled(enabled: Boolean)

    fun setWillNotDraw(willNotDraw: Boolean)

    fun setChildrenDrawingOrderEnabledPublic(enabled: Boolean)

    fun addView(child: View)

    fun getChildCount(): Int

    fun getChildAt(index: Int): View?

    fun getMeasuredWidth(): Int

    fun getMeasuredHeight(): Int

    fun getPaddingTop(): Int

    fun getPaddingBottom(): Int

    fun getPaddingLeft(): Int

    fun getPaddingStart(): Int

    fun getPaddingRight(): Int

    fun getPaddingEnd(): Int
}