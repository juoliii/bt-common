package com.bitian.common.util;

import org.apache.commons.codec.digest.DigestUtils;
/**
 * MD5加密
 * @author juoliii
 *
 */
public class MD5Util {

	/**
	 * hex 16进制
	 * @param str
	 * @return
	 */
	public static String md52Hex(String str){
		return DigestUtils.md5Hex(str);
	}
	
}
