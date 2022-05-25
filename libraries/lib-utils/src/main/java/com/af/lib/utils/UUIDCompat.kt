package com.af.lib.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import com.af.lib.compat.AndroidVersionCompat
import com.af.lib.compat.AndroidVersionCompatible
import java.lang.reflect.Method

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
 * 目的是让分布式系统中的所有元素，都能有唯一的辨识信息，
 * 而不需要通过中央控制端来做辨识信息的指定。
 * UUID 是指在一台机器上生成的数字，由当前日期和时间、时钟序列、全局唯一的 IEEE 机器识别号等几部分组合形成，
 * 它可以保证对在同一时空中的所有机器都是唯一的
 * UUID 在一般情况下很难生成一致的编码
 *
 * @author liangxiaxu@leyaoyao.com
 * @date 2022-05-25
 */
object UUIDCompat {

    private const val TAG = "uuid"



    suspend fun getUUID(context: Context): String? {

        AndroidVersionCompat.compat(object : AndroidVersionCompatible {

            override fun compatWithQ(): Boolean {
                TODO("")
            }

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


