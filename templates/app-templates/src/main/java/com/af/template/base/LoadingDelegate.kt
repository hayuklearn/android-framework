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
class LoadingDelegate(private val loadingable: Loadingable) {

    fun showLoading() {

        this.loadingable.showLoading()
    }

    fun hideLoading() {

        this.loadingable.hideLoading()
    }

    fun isLoading(): Boolean = this.loadingable.isLoading()
}