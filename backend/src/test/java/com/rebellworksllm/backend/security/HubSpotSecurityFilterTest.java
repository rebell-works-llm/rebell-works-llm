package com.rebellworksllm.backend.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class HubSpotSecurityFilterTest {

    private HubSpotSecurityFilter filter;
    private HubSpotSignatureValidator validator;

    @BeforeEach
    void setUp() {
        validator = mock(HubSpotSignatureValidator.class);
        filter = new HubSpotSecurityFilter(validator);
    }

    @Test
    @DisplayName("Valid signature should allow request")
    void validSignatureAllowsRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/hubspot/contacts/created");
        request.setContent("{\"objectId\": \"12345\"}".getBytes(StandardCharsets.UTF_8));
        request.addHeader("X-HubSpot-Request-Timestamp", String.valueOf(System.currentTimeMillis()));
        request.addHeader("X-HubSpot-Signature-v3", "valid-signature");

        when(validator.isValidSignature(any(), any(), any(), any(), any())).thenReturn(true);

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertEquals(200, response.getStatus());
        verify(chain).doFilter(any(), any());
    }

    @Test
    @DisplayName("Should reject request with invalid signature")
    void invalidSignatureShouldReject() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/hubspot/contacts/created");
        request.setContent("{\"objectId\": \"12345\"}".getBytes(StandardCharsets.UTF_8));
        request.addHeader("X-HubSpot-Request-Timestamp", String.valueOf(System.currentTimeMillis()));
        request.addHeader("X-HubSpot-Signature-v3", "invalid-signature");

        when(validator.isValidSignature(any(), any(), any(), any(), any())).thenReturn(false);

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertEquals(401, response.getStatus());
        assertEquals("Invalid HubSpot signature", response.getErrorMessage());
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("Should reject request with missing signature")
    void missingSignature() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/hubspot/contacts/created");
        request.setContent("{\"objectId\": \"12345\"}".getBytes(StandardCharsets.UTF_8));
        request.addHeader("X-HubSpot-Request-Timestamp", String.valueOf(System.currentTimeMillis()));

        when(validator.isValidSignature(any(), any(), any(), any(), any())).thenReturn(false);

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertEquals(401, response.getStatus());
        assertEquals("Missing signature headers", response.getErrorMessage());
        verify(chain, never()).doFilter(any(), any());
    }
}