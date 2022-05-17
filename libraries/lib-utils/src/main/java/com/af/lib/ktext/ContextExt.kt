package com.af.lib.ktext

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.TypedValue
import androidx.annotation.DimenRes
import androidx.annotation.RequiresApi
import com.af.lib.compat.AndroidVersionCompat
import com.af.lib.compat.AndroidVersionCompatible
import com.af.lib.utils.MD5
import com.af.lib.utils.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
 * @date 2022-04-27
 */

/**
 * 计算应用签名
 *
 * @param packageName 包名
 */
suspend fun Context.signature(packageName: String): String = withContext(Dispatchers.IO) {

    return@withContext suspendCoroutine {

        AndroidVersionCompat.compat(object : AndroidVersionCompatible {

            @RequiresApi(Build.VERSION_CODES.P)
            override fun compatWithP(): Boolean {

                val signingInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES).signingInfo
                val signatureArray = if (signingInfo.hasMultipleSigners()) {
                    signingInfo.apkContentsSigners
                } else {
                    signingInfo.signingCertificateHistory
                }
                it.resume(MD5.md5(signatureArray[0].toByteArray()))
                return true
            }

            @SuppressLint("PackageManagerGetSignatures")
            override fun compatWithDefault() {

                val signatureArray = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures;
                it.resume(MD5.md5(signatureArray[0].toByteArray()))
            }
        })
    }
}

/**
 * dp 转 px
 *
 * @param dp DP
 */
fun Context.dp2px(dp: Float) = (dp * resources.displayMetrics.density + 0.5f).toInt()

/**
 * px 转 dp
 *
 * @param px PX
 */
fun Context.px2dp(px: Float) = (px / resources.displayMetrics.density + 0.5f).toInt()

/**
 * sp 转 px
 *
 * @param sp SP
 */
fun Context.sp2px(sp: Float) = (sp * resources.displayMetrics.scaledDensity + 0.5f).toInt()

/**
 * px 转 sp
 *
 * @param px PX
 */
fun Context.px2sp(px: Float) = (px / resources.displayMetrics.scaledDensity + 0.5f).toInt()

/**
 * 获取 dimen 定义的数值
 *
 * 比如：<dimen name="item_height">48dp</dimen> 返回 48
 *
 * @param resId 资源 ID
 *
 * @return
 */
fun Context.getXMLValue(@DimenRes resId: Int): Int {

    val typedValue = TypedValue()
    resources.getValue(resId, typedValue, true)
    return TypedValue.complexToFloat(typedValue.data).toInt()
}

/**
 * 获取 dimen 定义的像素值
 *
 * @param resId 资源 ID
 *
 * @return
 */
fun Context.getPXFromDimenResId(@DimenRes resId: Int) = dp2px(getXMLValue(resId).toFloat())

/**
 * 获取屏幕宽度
 *
 * @return Int
 */
fun Context.getScreenWidth() = resources.displayMetrics.widthPixels

/**
 * 获取屏幕高度
 *
 * @return Int
 */
fun Context.getScreenHeight() = resources.displayMetrics.heightPixels

/**
 * 获取状态栏的高度
 *
 * @return Int
 */
fun Context.getStatusBarHeight(): Int {

    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else {
        0
    }
}

/**
 * 获取头部工具栏的高度
 *
 * @return Int
 */
fun Context.getToolbarHeight(): Int {

    val typedValue = TypedValue()
    return if (theme.resolveAttribute(R.attr.actionBarSize, typedValue, true)) {
        TypedValue.complexToDimensionPixelSize(typedValue.data, resources.displayMetrics)
    } else {
        0
    }
}

/**
 * 获取底部导航栏高度
 *
 * @return Int
 */
fun Context.getNavigationBarHeight(): Int {

    val rid: Int = resources.getIdentifier("config_showNavigationBar", "bool", "android")
    return if (rid != 0) {
        val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        resources.getDimensionPixelSize(resourceId)
    } else {
        0
    }
}