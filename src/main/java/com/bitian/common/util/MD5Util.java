package com.bitian.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sun.misc.BASE64Encoder;
/**
 * MD5加密
 * @author juoliii
 *
 */
public class MD5Util {
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
	
}
