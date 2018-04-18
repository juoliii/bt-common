package com.bitian.common.util;

import java.util.UUID;

/**
 * 主键生成辅助工具类
 * @author Administrator
 *
 */
public class PrimaryKeyUtil {
	
	/**
	 * 返回一个32个字母或者数组组成的字符串，此字符串唯一不会重复
	 * @return
	 */
	public static String getUUID(){
		UUID uuid=UUID.randomUUID();
		return uuid.toString().replace("-", "");
	}
	
	public static void main(String[] args) {
		System.out.println(getUUID());
	}
}
