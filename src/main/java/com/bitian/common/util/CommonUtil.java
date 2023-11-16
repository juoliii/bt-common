package com.bitian.common.util;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author admin
 */
public class CommonUtil {
	
	public static boolean isAjaxRequest(HttpServletRequest request) {
	    String requestType = request.getHeader("X-Requested-With");
	    if (requestType != null && requestType.equals("XMLHttpRequest")) {
	        return true;
	    } else {
	        return false;
	    }
	}
	
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

    public static String getParamFromUrl(String url,String id){
        if(null != url){
            if(!url.startsWith("http")){
                return url;
            }
            String[] arr = url.split("\\?");
            if(arr.length > 0){
                String params = arr[arr.length - 1];
                arr = params.split("\\&");
                if(arr.length > 0){
                    for(String str : arr){
                        String[] _arr = str.split("\\=");
                        if(_arr.length > 1){
                            if(_arr[0].equals(id)){
                                return _arr[1];
                            }
                        }
                    }
                }
            }
        }
        return "";
    }

}
