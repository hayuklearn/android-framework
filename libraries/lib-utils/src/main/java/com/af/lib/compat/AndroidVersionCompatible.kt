package com.af.lib.compat

import androidx.annotation.RequiresApi

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
 * @date 2022-05-17
 */
interface AndroidVersionCompatible {

    /**
     * @return Boolean 是否适配完毕
     */
    fun compatWithS(): Boolean = false

    fun compatWithR(): Boolean = false

    fun compatWithQ(): Boolean = false

    fun compatWithP(): Boolean = false

    fun compatWithO(): Boolean = false

    fun compatWithN(): Boolean = false

    fun compatWithM(): Boolean = false

    fun compatWithLollipop(): Boolean = false

    fun compatWithKitkat(): Boolean = false

    fun compatWithDefault() {}
}

