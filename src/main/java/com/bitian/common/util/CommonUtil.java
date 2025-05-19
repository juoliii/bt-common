package com.bitian.common.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static String getBasePathFromRequest(HttpServletRequest request){
        int port = request.getServerPort();
        String scheme = request.getHeader("X-Forwarded-Scheme");
        if(StringUtils.isEmpty(scheme)) {
            scheme = request.getScheme();
        }
        String basePath = scheme+"://"+request.getServerName()+(port != 80 ? ":" + port : "") + request.getContextPath();
        basePath = basePath.replaceAll("/+$", "");
        return basePath;
    }

    public static String readContentFromRequest(HttpServletRequest request) {
        try {
            BufferedReader br = request.getReader();
            String str;
            StringBuffer buffer = new StringBuffer();
            while((str = br.readLine()) != null){
                buffer.append(str);
            }
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 剔除字符串中的html标签
     * @param strHtml
     * @return
     */
    public static String stripHTML(String strHtml) {
        //剔出<html>的标签
        String txtcontent = strHtml.replaceAll("</?[^>]+>", "");
        //去除字符串中的空格,回车,换行符,制表符
        txtcontent = txtcontent.replaceAll("<a>\\s*|\t|\r|\n</a>", "");
        return txtcontent;
    }

    /**
     * 是否是中文
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        String regEx = "[\u4e00-\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(c + "");
        if (m.find())
            return true;
        return false;
    }

}
