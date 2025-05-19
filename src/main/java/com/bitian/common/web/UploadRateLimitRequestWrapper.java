package com.bitian.common.web;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

/**
 * 上传带宽控制
 * @author admin
 */
public class UploadRateLimitRequestWrapper extends HttpServletRequestWrapper {

    private long limit;

    /**
     * 构造函数
     * @param request HttpServletRequest
     * @param perSecondsSize 每秒传输字节数，如 1024 = 1kb/s
     */
    public UploadRateLimitRequestWrapper(HttpServletRequest request,long perSecondsSize) {
        super(request);
        limit=perSecondsSize;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new UploadRateLimitServletInputStream(super.getInputStream(),limit);
    }

}
