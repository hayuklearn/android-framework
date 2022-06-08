package com.af.lib.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
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
object IMEICompat {

    suspend fun getIMEI(context: Context): String {

        return suspendCoroutine { continuation ->

            AndroidVersionCompat.compat(object : AndroidVersionCompatible {

                override fun compatWithQ(): Boolean {
                    // 不支持反射获取 IMEI
                    continuation.resume("")
                    return true
                }

                override fun compatWithLollipop(): Boolean {
                    // 反射方式获取 IMEI
                    val telephonyManager = context.getSystemService(Activity.TELEPHONY_SERVICE) as TelephonyManager
                    val method = telephonyManager.javaClass.getMethod("getImei")
                    val imei = method.invoke(telephonyManager) as String
                    continuation.resume(imei)
                    return true
                }

                @SuppressLint("MissingPermission", "HardwareIds")
                override fun compatWithDefault() {

                    val telephonyManager = context.getSystemService(Activity.TELEPHONY_SERVICE) as TelephonyManager
                    val imei = telephonyManager.deviceId
                    continuation.resume(imei)
                }
            })
        }
    }
}