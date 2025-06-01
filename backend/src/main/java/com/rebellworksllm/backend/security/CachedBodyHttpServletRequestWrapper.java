package com.rebellworksllm.backend.security;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class CachedBodyHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private static final int MAX_BODY_SIZE = 1024 * 1024; // 1MB

    private final byte[] cachedBody;

    public CachedBodyHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        // Cache the body with size limit
        this.cachedBody = StreamUtils.copyToByteArray(request.getInputStream());
        if (cachedBody.length > MAX_BODY_SIZE) {
            throw new IOException("Request body exceeds maximum size of " + MAX_BODY_SIZE + " bytes");
        }
    }

    @Override
    public ServletInputStream getInputStream() {
        return new CachedBodyServletInputStream(this.cachedBody);
    }

    @Override
    public BufferedReader getReader() {
        InputStream inputStream = new ByteArrayInputStream(this.cachedBody);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        return new BufferedReader(inputStreamReader);
    }

    public String getBody() {
        return new String(cachedBody, StandardCharsets.UTF_8);
    }
}
