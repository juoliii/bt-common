package com.bitian.common.util;

import java.security.MessageDigest;

import org.apache.commons.codec.digest.DigestUtils;
import sun.misc.BASE64Encoder;
/**
 * MD5加密
 * @author juoliii
 *
 */
public class MD5Util {
	/**
	 * base64
	 * @param str
	 * @return
	 */
	public static String md5(String str){
		try {
			MessageDigest md=MessageDigest.getInstance("md5");
			byte[] b=md.digest(str.getBytes("UTF8"));
			BASE64Encoder encoder=new BASE64Encoder();
			return encoder.encode(b);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * hex 16进制
	 * @param str
	 * @return
	 */
	public static String md52Hex(String str){
		return DigestUtils.md5Hex(str);
	}
	
}
