package com.rebellworksllm.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class HubSpotSecurityFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(HubSpotSecurityFilter.class);

    private final HubSpotSignatureValidator signatureValidator;

    public HubSpotSecurityFilter(HubSpotSignatureValidator signatureValidator) {
        this.signatureValidator = signatureValidator;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            CachedBodyHttpServletRequestWrapper wrappedRequest = new CachedBodyHttpServletRequestWrapper(request);

            String method = wrappedRequest.getMethod();
            String fullUrl = request.getRequestURL().toString();
            String signature = request.getHeader("X-HubSpot-Signature-v3");
            String timestamp = request.getHeader("X-HubSpot-Request-Timestamp");
            String body = wrappedRequest.getBody();

            if (signature == null || timestamp == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing signature headers");
                return;
            }

            if (!signatureValidator.isValidSignature(method, fullUrl, body, timestamp, signature)) {
                logger.warn("Invalid HubSpot signature for request: {}", fullUrl);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid HubSpot signature");
                return;
            }

            filterChain.doFilter(wrappedRequest, response);
        } catch (Exception e) {
            logger.error("Error processing request", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error validating signature");
        }
    }
}