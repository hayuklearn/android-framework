package com.af.lib.utils

import java.security.MessageDigest

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
object MD5 {

    /**
     * 计算字符串 MD5
     *
     * @param source 输入字符串
     */
    fun md5(source: String) = md5(source.toByteArray())

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
}