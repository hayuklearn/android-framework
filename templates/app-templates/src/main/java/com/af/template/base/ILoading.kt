package com.af.template.base

import com.af.widget.loadingable.Loadingable

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
 * @author liangxiaxu@leyaoyao.com
 * @date 2022-04-29
 */
interface ILoading {

    /**
     * 显示加载动画
     *
     * @param block Boolean 是否阻塞前台界面
     * @return Unit
     */
    fun showLoading(block: Boolean = false)

    fun hideLoading()

    fun isLoading(): Boolean

    fun provideLoadingable(): Loadingable
}