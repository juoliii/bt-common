package com.bitian.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {
	private static final int MAX_AGE=60*60*24*7;
	
	public static void addCookie(String name,String value,HttpServletResponse response){
		addCookie(name, value, MAX_AGE, response);
	}
	
	public static void addCookie(String name,String value,int maxAge,HttpServletResponse response){
		try {
			Cookie cookie=new Cookie(name,URLEncoder.encode(value,"utf-8"));
			cookie.setMaxAge(maxAge);
			response.addCookie(cookie);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public static void removeCookie(String name,HttpServletResponse response){
		Cookie cookie=new Cookie(name,"");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
		
	}
	
	public static String findCookie(String name,HttpServletRequest request){
		Cookie[] cookies=request.getCookies();
		for (Cookie cookie : cookies) {
			if(name.equals(cookie.getName())){
				try {
					return URLDecoder.decode(cookie.getValue(),"utf-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
}
