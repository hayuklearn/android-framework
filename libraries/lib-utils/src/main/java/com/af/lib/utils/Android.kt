package com.af.lib.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.content.pm.SigningInfo
import android.os.Build
import android.util.Log
import java.security.MessageDigest

/**
 * Android 专用方法
 *
 * Created by hayukleung@gmail.com on 2021-09-10.
 */
class Android {

    companion object {

        /**
         * 计算应用签名
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
         * 计算 MD5
         */
        private fun md5(source: ByteArray): String {

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
         */
        fun dp2px(context: Context, dp: Float): Int {

            return (dp * context.resources.displayMetrics.density + 0.5f).toInt()
        }
    }
}
