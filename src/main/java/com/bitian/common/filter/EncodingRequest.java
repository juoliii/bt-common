package com.bitian.common.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class EncodingRequest extends HttpServletRequestWrapper {
	private String charSet;
	
	public EncodingRequest(HttpServletRequest request,String charSet) {
		super(request);
		this.charSet=charSet;
	}
	
	public String getParameter(String name) {
		return encoding(super.getParameter(name));
	}
	
	public String[] getParameterValues(String name) {
		String[] values=super.getParameterValues(name);
		for (int i = 0; values!=null&&i < values.length; i++) {
			values[i]=encoding(values[i]);
		}
		return values;
	}
	
	public String encoding(String src){
		try {
			return new String(src.getBytes("ISO8859-1"),charSet);
		} catch (Exception e) {
		}
		return src;
	}
	
}
