package com.lyy.database

import com.google.gson.Gson
import com.lyy.security.NativeCaller

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
 * @date 2022-03-02
 */
object Security {

    /**
     * 加密字符串
     *
     * @param decrypted String
     * @return String
     */
    fun encryptString(decrypted: String): String {

        return Base64.encode(NativeCaller.aesEncrypt(decrypted.toByteArray())).trim { it <= ' ' }
    }

    /**
     * 解密字符串
     *
     * @param encrypted String
     * @return String
     */
    fun decryptString(encrypted: String): String {

        return String(NativeCaller.aesDecrypt(Base64.decode(encrypted.trim { it <= ' ' })))
    }

    /**
     * 加密对象
     *
     * @param t T
     * @return String
     */
    inline fun <reified T> encrypt(t: T): String {

        val json = Gson().toJson(t)
        // return Key.encodeText(json, "123456781234567812345678", "12345678").trim { it <= ' ' }
        return encryptString(json)
    }

    /**
     * 解密对象
     *
     * @param encrypted String
     * @return T
     */
    inline fun <reified T> decrypt(encrypted: String): T {

        // val json = Key.decodeText(encrypted.trim { it <= ' ' }, "123456781234567812345678", "12345678")
        val json = decryptString(encrypted)
        return Gson().fromJson(json, T::class.java)
    }


    inline fun <reified T> toEncryptedItem(uniqueId: String, t: T): EncryptedItem {

        val type = T::class.java.simpleName
        return EncryptedItem("$type-$uniqueId", type, encrypt(t))
    }

    inline fun <reified T> fromEncryptedItem(item: EncryptedItem?): T? {

        if (null == item) return null
        return decrypt(item.content)
    }
}