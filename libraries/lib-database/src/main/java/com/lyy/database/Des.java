package com.lyy.database;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

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
public class Des {

    // 长度为 24 的密钥
    // private final static String secretKey = "01234567890123456789abcd";

    // 向量
    // private final static String IV = "01234567";

    /**
     * 加解密统一使用的编码方式
     */
    private final static String encoding = "utf-8";

    /**
     * 3DES 加密
     *
     * @param source 原文
     * @param key24 长度为 24 的密钥
     * @param iv8 长度为 8 的向量
     * @return
     */
    public static String encode(String source, final String key24, final String iv8) {

        try {
            Key desKey;
            DESedeKeySpec spec = new DESedeKeySpec(key24.getBytes());
            SecretKeyFactory factory = SecretKeyFactory.getInstance("desede");
            desKey = factory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
            IvParameterSpec ips = new IvParameterSpec(iv8.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, desKey, ips);
            byte[] encryptData = cipher.doFinal(source.getBytes(encoding));
            return Base64.encode(encryptData);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 3DES 解密
     *
     * @param encrypted 加密后的文本
     * @param key24 长度为 24 的密钥
     * @param iv8 长度为 8 的向量
     * @return 原文
     */
    public static String decode(String encrypted, final String key24, final String iv8) {

        try {
            Key desKey;
            DESedeKeySpec spec = new DESedeKeySpec(key24.getBytes());
            SecretKeyFactory factory = SecretKeyFactory.getInstance("desede");
            desKey = factory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
            IvParameterSpec ips = new IvParameterSpec(iv8.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, desKey, ips);
            byte[] decryptData = cipher.doFinal(Base64.decode(encrypted));
            return new String(decryptData, encoding);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
