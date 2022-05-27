package com.lyy.database;

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
 * @date 2022-02-28
 */
@Deprecated
public class Key {

    /**
     * 解码 - 文本
     *
     * @param encrypted
     * @param key24
     * @param iv8
     * @return
     */
    public static String decodeText(String encrypted, final String key24, final String iv8) {

        String decrypted = Des.decode(encrypted, key24, iv8);
        return decrypted;
    }

    /**
     * 编码 - 文本
     *
     * @param decrypted
     * @param key24
     * @param iv8
     * @return
     */
    public static String encodeText(String decrypted, final String key24, final String iv8) {

        String encrypted = Des.encode(decrypted, key24, iv8);
        return encrypted;
    }
}