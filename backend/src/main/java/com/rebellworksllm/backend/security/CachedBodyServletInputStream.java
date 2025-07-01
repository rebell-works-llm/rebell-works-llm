package com.rebellworksllm.backend.security;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.ByteArrayInputStream;

public class CachedBodyServletInputStream extends ServletInputStream {

    private final ByteArrayInputStream byteArrayInputStream;

    public CachedBodyServletInputStream(byte[] cachedRequest) {
        this.byteArrayInputStream = new ByteArrayInputStream(cachedRequest);
    }

    @Override
    public boolean isFinished() {
        return byteArrayInputStream.available() == 0;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener listener) {
        // Not required for this implementation
    }

    @Override
    public int read() {
        return byteArrayInputStream.read();
    }
}