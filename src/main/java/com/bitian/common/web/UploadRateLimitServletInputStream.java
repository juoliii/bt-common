package com.bitian.common.web;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;

/**
 * 流量控制 inputStream
 * @author admin
 */
public class UploadRateLimitServletInputStream extends ServletInputStream {

    ServletInputStream inputStream;

    long rateLimit=1 * 1024 * 1024; // 限速，单位为字节/秒，默认1M/秒

    public UploadRateLimitServletInputStream(ServletInputStream inputStream,long rateLimit) {
        this.inputStream = inputStream;
        this.rateLimit = rateLimit;
    }

    public UploadRateLimitServletInputStream(ServletInputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public boolean isFinished() {
        return inputStream.isFinished();
    }

    @Override
    public boolean isReady() {
        return inputStream.isReady();
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        inputStream.setReadListener(readListener);
    }

    private static final long INTERVAL = 1000; // 1秒

    private long lastReadTime;
    private long bytesRead;

    private void throttle() {
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - lastReadTime;

        if (elapsed < INTERVAL) {
            long timeToSleep = INTERVAL - elapsed;
            long bytesToSleep = (timeToSleep * rateLimit) / 1000;

            if (bytesRead >= bytesToSleep) {
                try {
                    Thread.sleep(timeToSleep);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                lastReadTime = System.currentTimeMillis();
                bytesRead = 0;
            }
        } else {
            lastReadTime = currentTime;
            bytesRead = 0;
        }
    }

    @Override
    public int read() throws IOException {
        throttle();
        int byteRead = inputStream.read();
        if (byteRead != -1) {
            bytesRead++;
        }
        return byteRead;
    }

}