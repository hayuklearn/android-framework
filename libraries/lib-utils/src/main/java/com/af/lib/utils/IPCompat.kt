package com.af.lib.utils

import android.content.Context
import com.af.lib.compat.AndroidVersionCompat
import com.af.lib.compat.AndroidVersionCompatible


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
 * @date 2022-05-23
 */
object IPCompat {

    suspend fun getIPv4(context: Context): String? {

        AndroidVersionCompat.compat(object : AndroidVersionCompatible {

            override fun compatWithLollipop(): Boolean {
                TODO("")
            }

            override fun compatWithDefault() {
                TODO("")
            }
        })
        TODO("")
    }
}