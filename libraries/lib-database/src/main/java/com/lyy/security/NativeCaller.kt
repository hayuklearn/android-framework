package com.lyy.security

import android.content.Context

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
 * @date 2022-03-09
 */
object NativeCaller {

    init {
        System.loadLibrary("crypto")
        System.loadLibrary("security")
    }

    /**
     * AES 加密
     *
     * @param src ByteArray?
     * @return ByteArray?
     */
    external fun aesEncrypt(src: ByteArray?): ByteArray

    /**
     * AES 解密
     *
     * @param src ByteArray?
     * @return ByteArray?
     */
    external fun aesDecrypt(src: ByteArray?): ByteArray

    /**
     * RSA 私钥加密
     *
     * @param src ByteArray?
     * @return ByteArray?
     */
    external fun rsaEncryptPrivate(src: ByteArray?): ByteArray

    /**
     * RSA 公钥解密
     *
     * @param src ByteArray?
     * @return ByteArray?
     */
    external fun rsaDecryptPublic(src: ByteArray?): ByteArray

    /**
     * RSA 公钥加密
     *
     * @param src ByteArray?
     * @return ByteArray?
     */
    external fun rsaEncryptPublic(src: ByteArray?): ByteArray

    /**
     * RSA 私钥解密
     *
     * @param src ByteArray?
     * @return ByteArray?
     */
    external fun rsaDecryptPrivate(src: ByteArray?): ByteArray

    /**
     * RSA 私钥签名
     *
     * @param src ByteArray?
     * @return ByteArray?
     */
    external fun rsaSignPrivate(src: ByteArray?): ByteArray

    /**
     * RSA 公钥验证签名
     *
     * @param src ByteArray?
     * @param sign ByteArray?
     * @return Int 1 - 成功 0 - 失败
     */
    external fun rsaVerifyPublic(src: ByteArray?, sign: ByteArray?): Int

    /**
     * 用 apk 签名的 hash1 进行验证，检查从 apk 获取的 hash1 的值与我们配置的常量 hash1 是否一致
     *
     * @param context Context?
     * @return Boolean
     */
    // external fun verifyApkSignHash1(context: Context?): Boolean
}

