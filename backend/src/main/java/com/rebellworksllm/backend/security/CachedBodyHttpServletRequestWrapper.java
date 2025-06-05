package com.rebellworksllm.backend.security;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class CachedBodyHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private static final int DEFAULT_BUFFER_SIZE = 8192;

    private final byte[] cachedBody;

    public CachedBodyHttpServletRequestWrapper(HttpServletRequest request, int maxSize) throws IOException {
        super(request);
        this.cachedBody = readInputStream(request.getInputStream(), maxSize);
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

    public byte[] getCachedBodyAsByteArray() {
        return cachedBody;
    }

    private static byte[] readInputStream(InputStream inputStream, int maxSize) throws IOException {
        if (inputStream == null) {
            return new byte[0];
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int totalBytesRead = 0;
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            totalBytesRead += bytesRead;
            if (totalBytesRead > maxSize) {
                throw new IOException("Request body exceeds maximum allowed size of " + maxSize + " bytes");
            }
            outputStream.write(buffer, 0, bytesRead);
        }

        return outputStream.toByteArray();
    }
}
