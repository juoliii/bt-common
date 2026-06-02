package com.bitian.common.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Objects;

/**
 * DES加密/解密工具类
 * <p>
 * 采用 DES/ECB/PKCS5Padding 算法，密钥长度为 8 字节。
 * 本类实例是<b>线程安全</b>的——每次加解密操作都在方法内创建独立的 {@link Cipher}，
 * 避免多线程共享 Cipher 实例导致的数据错乱。
 * </p>
 *
 * <pre>{@code
 * DesUtil des = DesUtil.getInstance("mySecret");
 * String encrypted = des.encrypt("hello");
 * String decrypted = des.decrypt(encrypted);
 * }</pre>
 */
public class DesUtil {

    private static final String ALGORITHM = "DES";
    private static final String TRANSFORMATION = "DES/ECB/PKCS5Padding";

    /**
     * 加密工具
     */
    private Cipher encryptCipher = null;

    /**
     * 解密工具
     */
    private Cipher decryptCipher = null;

    /**
     * 预生成的密钥，用于线程安全地按需创建 Cipher
     */
    private final SecretKey secretKey;

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
        Objects.requireNonNull(strKey, "密钥不能为空");
        //创建密钥规则
        DESKeySpec keySpec = new DESKeySpec(strKey.getBytes(StandardCharsets.UTF_8));
        //创建密钥工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        //按照密钥规则生成密钥
        secretKey = keyFactory.generateSecret(keySpec);
        encryptCipher = Cipher.getInstance(TRANSFORMATION);
        encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);
        decryptCipher = Cipher.getInstance(TRANSFORMATION);
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
        return new String(decrypt(Base64.decodeBase64(strIn)), StandardCharsets.UTF_8);
    }

    // ==================== 线程安全版本 ====================

    /**
     * 加密字节数组（线程安全）
     * <p>每次调用创建独立的 Cipher 实例，适用于多线程并发场景。</p>
     *
     * @param arrB 需加密的字节数组
     * @return 加密后的字节数组
     * @throws GeneralSecurityException 加密失败时抛出
     */
    public byte[] encryptSafe(byte[] arrB) throws GeneralSecurityException {
        Objects.requireNonNull(arrB, "待加密数据不能为空");
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(arrB);
    }

    /**
     * 解密字节数组（线程安全）
     * <p>每次调用创建独立的 Cipher 实例，适用于多线程并发场景。</p>
     *
     * @param arrB 需解密的字节数组
     * @return 解密后的字节数组
     * @throws GeneralSecurityException 解密失败时抛出
     */
    public byte[] decryptSafe(byte[] arrB) throws GeneralSecurityException {
        Objects.requireNonNull(arrB, "待解密数据不能为空");
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(arrB);
    }

    /**
     * 加密字符串（线程安全）
     * <p>每次调用创建独立的 Cipher 实例，适用于多线程并发场景。</p>
     *
     * @param strIn 需加密的字符串
     * @return Base64 编码的加密字符串
     * @throws GeneralSecurityException 加密失败时抛出
     */
    public String encryptSafe(String strIn) throws GeneralSecurityException {
        Objects.requireNonNull(strIn, "待加密字符串不能为空");
        return Base64.encodeBase64String(encryptSafe(strIn.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * 解密字符串（线程安全）
     * <p>每次调用创建独立的 Cipher 实例，适用于多线程并发场景。</p>
     *
     * @param strIn Base64 编码的加密字符串
     * @return 解密后的明文字符串
     * @throws GeneralSecurityException 解密失败时抛出
     */
    public String decryptSafe(String strIn) throws GeneralSecurityException {
        Objects.requireNonNull(strIn, "待解密字符串不能为空");
        return new String(decryptSafe(Base64.decodeBase64(strIn)), StandardCharsets.UTF_8);
    }
}