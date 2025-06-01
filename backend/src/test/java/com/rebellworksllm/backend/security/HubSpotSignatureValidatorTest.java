package com.rebellworksllm.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.rebellworksllm.backend.utils.HttpTestUtils.DUMMY_CLIENT_SECRET;
import static com.rebellworksllm.backend.utils.HttpTestUtils.generateSignature;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class HubSpotSignatureValidatorTest {

    private HubSpotSignatureValidator validator;

    @BeforeEach
    void setUp() {
        validator = new HubSpotSignatureValidator(DUMMY_CLIENT_SECRET);
    }

    @Test
    @DisplayName("Valid signature should allow request")
    void validSignatureAllowsRequest() {
        String method = "POST";
        String url = "http://localhost:8080/api/v1/hubspot/contacts/created";
        String body = "{\"objectId\": \"12345\"}";
        String timestamp = String.valueOf(System.currentTimeMillis());

        String signature = generateSignature(method, url, body, timestamp);
        System.out.println(signature);

        boolean result = validator.isValidSignature(method, url, body, timestamp, signature);
        assertTrue(result);
    }

    @Test
    @DisplayName("Should reject request with invalid signature")
    void invalidSignatureShouldReject() {
        String method = "POST";
        String url = "http://localhost:8080/api/v1/hubspot/contacts/created";
        String body = "{\"objectId\": \"12345\"}";
        String timestamp = String.valueOf(System.currentTimeMillis());

        boolean result = validator.isValidSignature(method, url, body, timestamp, "wrong-signature");
        assertFalse(result);
    }

    @Test
    @DisplayName("Should reject request for outdated timestamp")
    void missingSignature() {
        String method = "POST";
        String url = "http://localhost:8080/api/v1/hubspot/contacts/created";
        String body = "{\"objectId\": \"12345\"}";
        String oldTimestamp = String.valueOf(System.currentTimeMillis() - (10 * 60 * 1000)); // 10 min ago

        String signature = generateSignature(method, url, body, oldTimestamp);
        boolean result = validator.isValidSignature(method, url, body, oldTimestamp, signature);
        assertFalse(result);
    }
}