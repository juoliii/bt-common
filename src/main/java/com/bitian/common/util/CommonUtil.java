package com.bitian.common.util;

import javax.servlet.http.HttpServletRequest;

public class CommonUtil {
	
	/**
	 * 判断是否为Ajax请求
	 * @param request   HttpServletRequest
	 * @return  是true, 否false
	 */
	public static boolean isAjaxRequest(HttpServletRequest request) {
	    String requestType = request.getHeader("X-Requested-With");
	    if (requestType != null && requestType.equals("XMLHttpRequest")) {
	        return true;
	    } else {
	        return false;
	    }
	}
	
	/**
	 * 获取servlet访问IP
	 * @param request
	 * @return
	 */
	public static String getRequestRemoteIp(HttpServletRequest request){
		String ip = request.getHeader("X-Forwarded-For");  
  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
                ip = request.getHeader("Proxy-Client-IP");  
            }  
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
                ip = request.getHeader("WL-Proxy-Client-IP");  
            }  
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
                ip = request.getHeader("HTTP_CLIENT_IP");  
            }  
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
            }  
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
                ip = request.getRemoteAddr();  
            }  
        } else if (ip.length() > 15) {
            String[] ips = ip.split(",");  
            for (int index = 0; index < ips.length; index++) {
                String strIp = (String) ips[index];
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;  
                    break;  
                }  
            }  
        }  
        return ip;  
	}
}
