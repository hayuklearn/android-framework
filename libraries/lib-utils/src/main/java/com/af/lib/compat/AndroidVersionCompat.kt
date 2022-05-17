package com.af.lib.compat

import android.annotation.SuppressLint
import android.os.Build

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
object AndroidVersionCompat {

    @SuppressLint("ObsoleteSdkInt")
    fun compat(compatible: AndroidVersionCompatible) {
        // ... in the future
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) and compatible.compatWithS()) {
            return
        }
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) and compatible.compatWithR()) {
            return
        }
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) and compatible.compatWithQ()) {
            return
        }
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) and compatible.compatWithP()) {
            return
        }
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) and compatible.compatWithO()) {
            return
        }
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) and compatible.compatWithN()) {
            return
        }
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) and compatible.compatWithM()) {
            return
        }
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) and compatible.compatWithLollipop()) {
            return
        }
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) and compatible.compatWithKitkat()) {
            return
        }
        // ... out of history
        compatible.compatWithDefault()
    }
}