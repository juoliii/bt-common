package com.bitian.common.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * des加密
 */
public class DesUtil {

    /**
     * 加密工具
     */
    private Cipher encryptCipher = null;

    /**
     * 解密工具
     */
    private Cipher decryptCipher = null;

    public static DesUtil getInstance(String key) throws Exception {
        return new DesUtil(key);
    }

    /**
     * 指定密钥构造方法
     *
     * @param strKey 指定的密钥
     * @throws Exception
     */
    public DesUtil(String strKey) throws Exception {
        //创建密钥规则
        DESKeySpec keySpec = new DESKeySpec(strKey.getBytes(StandardCharsets.UTF_8));
        //创建密钥工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        //按照密钥规则生成密钥
        SecretKey secretKey = keyFactory.generateSecret(keySpec);
        encryptCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);
        decryptCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        decryptCipher.init(Cipher.DECRYPT_MODE, secretKey);
    }

    /**
     * 加密字节数组
     *
     * @param arrB 需加密的字节数组
     * @return 加密后的字节数组
     * @throws Exception
     */
    public byte[] encrypt(byte[] arrB) throws Exception {
        return encryptCipher.doFinal(arrB);
    }

    /**
     * 解密字节数组
     *
     * @param arrB 需解密的字节数组
     * @return 解密后的字节数组
     * @throws Exception
     */
    public byte[] decrypt(byte[] arrB) throws Exception {
        return decryptCipher.doFinal(arrB);
    }

    /**
     * 加密字符串
     *
     * @param strIn 需加密的字符串
     * @return 加密后的字符串
     * @throws Exception
     */
    public String encrypt(String strIn) throws Exception {
        return Base64.encodeBase64String(encrypt(strIn.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * 解密字符串
     *
     * @param strIn 需解密的字符串
     * @return 解密后的字符串
     * @throws Exception
     */
    public String decrypt(String strIn) throws Exception {
        return new String(decrypt(Base64.decodeBase64(strIn)),"utf-8");
    }

}