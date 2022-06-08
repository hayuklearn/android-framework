package com.af.lib.utils

import android.annotation.SuppressLint
import android.os.Build
import android.os.SystemClock
import androidx.annotation.RequiresApi
import com.af.lib.compat.AndroidVersionCompat
import com.af.lib.compat.AndroidVersionCompatible
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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
 * @date 2022-06-08
 */
object SNCompat {

    suspend fun getSN(): String {

        return suspendCoroutine { continuation ->

            AndroidVersionCompat.compat(object : AndroidVersionCompatible {

                override fun compatWithQ(): Boolean {

                    compatWithDefault()
                    return true
                }

                @SuppressLint("MissingPermission")
                @RequiresApi(Build.VERSION_CODES.O)
                override fun compatWithO(): Boolean {
                    val serial = Build.getSerial()
                    return if (serial.isNullOrEmpty() || Build.UNKNOWN == serial) {
                        false
                    } else {
                        continuation.resume(serial)
                        true
                    }
                }

                @SuppressLint("HardwareIds")
                override fun compatWithDefault() {
                    var serial = Build.SERIAL
                    if (serial.isNullOrEmpty() || Build.UNKNOWN == serial) {
                        // 通过反射反复获取
                        serial = SystemProperties.getRoSerialno()
                        if (serial.isNotEmpty() && Build.UNKNOWN != serial) {
                            continuation.resume(serial)
                            return
                        }
                        SystemClock.sleep(50)
                        serial = SystemProperties.getRoBootSerialno()
                        if (serial.isNotEmpty() && Build.UNKNOWN != serial) {
                            continuation.resume(serial)
                            return
                        }
                        continuation.resume(serial)
                    }
                }
            })
        }
    }
}