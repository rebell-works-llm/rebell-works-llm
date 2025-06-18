package com.rebellworksllm.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rebellworksllm.backend.hubspot.config.HubSpotCredentials;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.nio.charset.StandardCharsets;

import static com.rebellworksllm.backend.security.SecurityTestConstants.*;
import static com.rebellworksllm.backend.security.utils.SignatureUtils.generateSignature;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class HubSpotSecurityFilterTest {

    private HubSpotSecurityFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain chain;

    @BeforeEach
    void setUp() {
        HubSpotCredentials hubSpotCredentials = new HubSpotCredentials();
        hubSpotCredentials.setClientSecret(TEST_CLIENT_SECRET);
        ObjectMapper objectMapper = new ObjectMapper();
        filter = new HubSpotSecurityFilter(objectMapper, hubSpotCredentials, 300_000, 1024 * 1024);

        request = new MockHttpServletRequest("POST", TEST_CONTACT_CREATION_URI);
        request.setContent("{\"objectId\": \"12345\"}".getBytes(StandardCharsets.UTF_8));
        request.setServerName("example.com");

        response = new MockHttpServletResponse();
        chain = mock(FilterChain.class);
    }

    @Test
    @DisplayName("Valid request should pass")
    void validRequestShouldPass() throws Exception {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String source = "POSThttps://example.com" + TEST_CONTACT_CREATION_URI + "{\"objectId\":\"12345\"}" + timestamp;
        String signature = generateSignature(source, TEST_CLIENT_SECRET);
        request.addHeader("X-HubSpot-Request-Timestamp", timestamp);
        request.addHeader("X-HubSpot-Signature-v3", signature);

        filter.doFilter(request, response, chain);

        assertEquals(200, response.getStatus());
        verify(chain).doFilter(any(), any());
    }

    @Test
    @DisplayName("Missing headers should be rejected")
    void denyMissingHeaders() throws Exception {
        // Missing both headers
        filter.doFilter(request, response, chain);

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Missing signature or timestamp header"));
        verify(chain, never()).doFilter(any(), any());

        // Missing signature only
        response = new MockHttpServletResponse();
        request.addHeader("X-HubSpot-Request-Timestamp", String.valueOf(System.currentTimeMillis()));
        filter.doFilter(request, response, chain);

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Missing signature or timestamp header"));
        verify(chain, never()).doFilter(any(), any());

        // Missing timestamp only
        response = new MockHttpServletResponse();
        request = new MockHttpServletRequest("POST", TEST_CONTACT_CREATION_URI);
        request.setContent("{\"objectId\": \"12345\"}".getBytes(StandardCharsets.UTF_8));
        request.setServerName("example.com");
        request.addHeader("X-HubSpot-Signature-v3", "some-signature");
        filter.doFilter(request, response, chain);

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Missing signature or timestamp header"));
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("Too old timestamp should be rejected")
    void denyTooOldTimestamp() throws Exception {
        String timestamp = String.valueOf(System.currentTimeMillis() - 6 * 60 * 1000); // 6 minutes ago
        String source = "POSThttps://example.com" + TEST_CONTACT_CREATION_URI + "{\"objectId\":\"12345\"}" + timestamp;
        String signature = generateSignature(source, TEST_CLIENT_SECRET);
        request.addHeader("X-HubSpot-Request-Timestamp", timestamp);
        request.addHeader("X-HubSpot-Signature-v3", signature);

        filter.doFilter(request, response, chain);

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Timestamp is outside allowed range"));
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("Invalid signature should be rejected")
    void denyInvalidSignature() throws Exception {
        String timestamp = String.valueOf(System.currentTimeMillis());
        request.addHeader("X-HubSpot-Request-Timestamp", timestamp);
        request.addHeader("X-HubSpot-Signature-v3", "invalid-signature");

        filter.doFilter(request, response, chain);

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Invalid HubSpot signature"));
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("Malformed timestamp should be rejected")
    void denyMalformedTimestamp() throws Exception {
        request.addHeader("X-HubSpot-Request-Timestamp", "not-a-number");
        request.addHeader("X-HubSpot-Signature-v3", "some-signature");

        filter.doFilter(request, response, chain);

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Invalid timestamp format"));
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("Large request body should be rejected")
    void denyLargeRequestBody() throws Exception {
        byte[] largeBody = new byte[2 * 1024 * 1024]; // 2MB, exceeding 1MB limit
        request.setContent(largeBody);
        request.addHeader("X-HubSpot-Request-Timestamp", String.valueOf(System.currentTimeMillis()));
        request.addHeader("X-HubSpot-Signature-v3", "some-signature");

        filter.doFilter(request, response, chain);

        assertEquals(413, response.getStatus());
        assertTrue(response.getContentAsString().contains("Request body exceeds maximum allowed size"));
        verify(chain, never()).doFilter(any(), any());
    }
}