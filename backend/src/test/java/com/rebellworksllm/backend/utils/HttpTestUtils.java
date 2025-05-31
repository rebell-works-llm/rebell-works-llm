package com.rebellworksllm.backend.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class HttpTestUtils {

    private HttpTestUtils() {
    }

    public static final String DUMMY_CLIENT_SECRET = "test-client-secret-123";

    public static String generateSignature(String method, String url, String body, String timestamp) {
        return generateSignature(method, url, body, timestamp, DUMMY_CLIENT_SECRET);
    }

    public static String generateSignature(String method, String url, String body, String timestamp, String secret) {
        try {
            String source = method.toUpperCase() + url + body + timestamp;
            System.out.println(source);
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] hash = mac.doFinal(source.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate signature", e);
        }
    }
}
