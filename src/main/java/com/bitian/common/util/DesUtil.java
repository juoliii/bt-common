package com.bitian.common.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * des加密（ECB/Nopaddingm模式）
 */
public class DesUtil {

    /**
     * 字符串默认键值
     */
    private static String strDefaultKey = "juoliiibitian";

    /**
     * 加密工具
     */
    private Cipher encryptCipher = null;

    /**
     * 解密工具
     */
    private Cipher decryptCipher = null;

    public static String byteArr2Base64Str(byte[] arrB) throws Exception {
        byte b[]=Base64.encodeBase64(arrB);
        return new String(b,"utf-8");
    }

    public static byte[] base64Str2ByteArr(String strIn) throws Exception {
        return Base64.decodeBase64(strIn);
    }

    /**
     * 默认构造方法，使用默认密钥
     *
     * @throws Exception
     */
    public DesUtil() throws Exception {
        this(strDefaultKey);
    }

    /**
     * 指定密钥构造方法
     *
     * @param strKey 指定的密钥
     * @throws Exception
     */
    public DesUtil(String strKey) throws Exception {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
        //创建密钥规则
        DESKeySpec keySpec = new DESKeySpec(strKey.getBytes());
        //创建密钥工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        //按照密钥规则生成密钥
        SecretKey secretKey = keyFactory.generateSecret(keySpec);
        encryptCipher = Cipher.getInstance("DES/ECB/NoPadding");
        encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);
        decryptCipher = Cipher.getInstance("DES/ECB/NoPadding");
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
        return byteArr2Base64Str(encrypt(strIn.getBytes()));
    }

    /**
     * 解密字符串
     *
     * @param strIn 需解密的字符串
     * @return 解密后的字符串
     * @throws Exception
     */
    public String decrypt(String strIn) throws Exception {
        return new String(decrypt(base64Str2ByteArr(strIn)),"utf-8");
    }

    public String encryptWithZeroPadding(String content) throws Exception{
        byte []b=encryptCipher.doFinal(addZero(content.getBytes()));
        return byteArr2Base64Str(b);
    }

    public static byte[] addZero(byte[] data) {
        byte[] dataByte = data;

        if (data.length % 8 != 0) {
            byte[] temp = new byte[8 - data.length % 8];
            dataByte = byteMerger(data, temp);
        }
        return dataByte;
    }

    public static byte[] byteMerger(byte[] bt1, byte[] bt2) {
        byte[] bt3 = new byte[bt1.length + bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }

    public static void main(String[] args) {
        try {
            DesUtil desUtil = new DesUtil("yuanheclient");
            System.out.println(desUtil.decrypt("0z6dpOqwDjWd6NlUuG/YNPOiqD6VsByq3Gkldair1URO+BXsjDh9brbg2pWBan42NqwMvqZa/uefKKX+qEZLIzWDvePnIvcTRSqZ3A2xi7Y="));
            //System.out.println(desUtil.encrypt("hahaha"));
            //System.out.println(desUtil.decrypt("0YaVkBZPMgg="));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}