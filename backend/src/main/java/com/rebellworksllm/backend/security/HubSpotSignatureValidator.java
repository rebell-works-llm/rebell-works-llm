package com.rebellworksllm.backend.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Component
public class HubSpotSignatureValidator {

    private static final Logger logger = LoggerFactory.getLogger(HubSpotSignatureValidator.class);
    private static final long TIMESTAMP_TOLERANCE_MS = 5 * 60 * 1000; // 5 minutes

    private final String clientSecret;

    public HubSpotSignatureValidator(@Value("${hubspot.client-secret}") String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public boolean isValidSignature(String method, String fullUrl, String body, String timestamp, String signature) {
        try {
            long requestTime = Long.parseLong(timestamp);
            long currentTime = System.currentTimeMillis();
            if (Math.abs(currentTime - requestTime) > TIMESTAMP_TOLERANCE_MS) {
                logger.warn("Invalid timestamp: requestTime={}, currentTime={}", requestTime, currentTime);
                return false;
            }

            String safeBody = (body != null ? body : "");
            String source = method.toUpperCase() + fullUrl + safeBody + timestamp;
            System.out.println(source);
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(clientSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(source.getBytes(StandardCharsets.UTF_8));
            String computedSignature = Base64.getEncoder().encodeToString(hash);
            System.out.println(computedSignature);

            boolean isValid = MessageDigest.isEqual(computedSignature.getBytes(StandardCharsets.UTF_8),
                    signature.getBytes(StandardCharsets.UTF_8));
            logger.debug("Signature valid: {}", isValid);

            return isValid;
        } catch (Exception e) {
            logger.error("Error validating signature", e);
            return false;
        }
    }
}