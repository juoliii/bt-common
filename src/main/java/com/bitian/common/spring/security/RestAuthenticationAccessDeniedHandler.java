package com.bitian.common.spring.security;

import com.alibaba.fastjson.JSON;
import com.bitian.common.dto.ResultJson;
import com.bitian.common.enums.ResultCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class RestAuthenticationAccessDeniedHandler implements AccessDeniedHandler {
	
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse response, AccessDeniedException e) throws IOException {
    	//登陆状态下，权限不足执行该方法
        response.setStatus(200);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter printWriter = response.getWriter();
        String body = JSON.toJSONString(ResultJson.failure(ResultCode.FORBIDDEN, e.getMessage()));
        printWriter.write(body);
        printWriter.flush();
    }
}

