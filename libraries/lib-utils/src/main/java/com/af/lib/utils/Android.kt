package com.af.lib.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.content.pm.SigningInfo
import android.os.Build
import android.util.Log
import android.util.TypedValue
import androidx.annotation.DimenRes
import java.security.MessageDigest

/**
 * Android 专用方法
 *
 * @author hayukleung
 * @date 2021-09-10
 */
object Android {

    /**
     * 计算应用签名
     *
     * @param context 上下文
     * @param packageName 包名
     */
    @SuppressLint("PackageManagerGetSignatures")
    fun signature(context: Context, packageName: String): String {

        val signatureArray: Array<Signature>
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val signingInfo: SigningInfo = context.packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNING_CERTIFICATES
            ).signingInfo
            signatureArray = if (signingInfo.hasMultipleSigners()) {
                signingInfo.apkContentsSigners
            } else {
                signingInfo.signingCertificateHistory
            }
        } else {
            signatureArray = context.packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            ).signatures;
        }
        for (signature in signatureArray) {
            Log.d(
                "Android - signature",
                md5(signature.toByteArray())
            )
        }
        return md5(signatureArray[0].toByteArray())
    }

    /**
     * 计算字符串 MD5
     *
     * @param source 输入字符串
     */
    fun md5(source: String) {

        md5(source.toByteArray())
    }

    /**
     * 计算字节数组 MD5
     *
     * @param source 输入字节数组
     */
    fun md5(source: ByteArray): String {

        val charArray = charArrayOf(
            '0',
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7',
            '8',
            '9',
            'a',
            'b',
            'c',
            'd',
            'e',
            'f'
        )
        val afterMD5 = MessageDigest.getInstance("MD5").digest(source)
        val size: Int = afterMD5.size
        val temp = CharArray(size * 2)
        var index = 0
        var cursor = 0
        while (index < size) {
            val currentByte = afterMD5[index]
            val tempCursor = cursor + 1
            temp[cursor] = charArray[currentByte.toInt() ushr 4 and 15]
            cursor = tempCursor + 1
            temp[tempCursor] = charArray[currentByte.toInt() and 15]
            ++index
        }
        return String(temp)
    }

    /**
     * dp 转 px
     *
     * @param context 上下文
     * @param dp DP
     */
    fun dp2px(context: Context, dp: Float): Int {

        return (dp * context.resources.displayMetrics.density + 0.5f).toInt()
    }

    /**
     * px 转 dp
     *
     * @param context 上下文
     * @param px PX
     */
    fun px2dp(context: Context, px: Float): Int {

        return (px / context.resources.displayMetrics.density + 0.5f).toInt()
    }

    /**
     * sp 转 px
     *
     * @param context 上下文
     * @param sp SP
     */
    fun sp2px(context: Context, sp: Float): Int {

        return (sp * context.resources.displayMetrics.scaledDensity + 0.5f).toInt()
    }

    /**
     * px 转 sp
     *
     * @param context 上下文
     * @param px PX
     */
    fun px2sp(context: Context, px: Float): Int {

        return (px / context.resources.displayMetrics.scaledDensity + 0.5f).toInt()
    }

    /**
     * 获取 dimen 定义的数值
     *
     * 比如：<dimen name="item_height">48dp</dimen> 返回 48
     *
     * @param context 上下文
     * @param resId 资源 ID
     *
     * @return
     */
    fun getXMLValue(context: Context, @DimenRes resId: Int): Int {

        val typedValue = TypedValue()
        context.resources.getValue(resId, typedValue, true)
        return TypedValue.complexToFloat(typedValue.data).toInt()
    }

    /**
     * 获取 dimen 定义的像素值
     *
     * @param context 上下文
     * @param resId 资源 ID
     *
     * @return
     */
    fun getPXFromDimenResId(context: Context, @DimenRes resId: Int): Int {

        return dp2px(context, getXMLValue(context, resId).toFloat())
    }

    /**
     * 获取屏幕宽度
     *
     * @param context Context
     * @return Int
     */
    fun getScreenWidth(context: Context): Int {

        return context.resources.displayMetrics.widthPixels
    }

    /**
     * 获取屏幕高度
     *
     * @param context Context
     * @return Int
     */
    fun getScreenHeight(context: Context): Int {

        return context.resources.displayMetrics.heightPixels
    }

    /**
     * 获取状态栏的高度
     *
     * @param context Context
     * @return Int
     */
    fun getStatusBarHeight(context: Context): Int {

        var statusBarHeight = 0

        val resourceId = context.resources
            .getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = context.resources.getDimensionPixelSize(resourceId)
        }

        return statusBarHeight
    }

    /**
     * 获取头部工具栏的高度
     *
     * @param context Context
     * @return Int
     */
    fun getToolbarHeight(context: Context): Int {

        val typedValue = TypedValue()

        var actionBarHeight = 0
        if (context.theme.resolveAttribute(R.attr.actionBarSize, typedValue, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(
                typedValue.data,
                context.resources.displayMetrics
            )
        }
        return actionBarHeight
    }

    /**
     * 获取底部导航栏高度
     *
     * @param context Context
     * @return Int
     */
    fun getNavigationBarHeight(context: Context): Int {

        val rid: Int = context.resources.getIdentifier("config_showNavigationBar", "bool", "android")
        return if (rid != 0) {
            val resourceId: Int = context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
            context.resources.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }
}
