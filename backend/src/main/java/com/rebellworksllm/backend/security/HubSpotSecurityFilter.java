package com.rebellworksllm.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rebellworksllm.backend.hubspot.config.HubSpotCredentials;
import com.rebellworksllm.backend.matching.presentation.dto.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static com.rebellworksllm.backend.security.utils.SignatureUtils.generateSignature;

@Component
public class HubSpotSecurityFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(HubSpotSecurityFilter.class);

    private final ObjectMapper objectMapper;
    private final HubSpotCredentials hubSpotCredentials;
    private final long timestampToleranceMs;
    private final int maxRequestSize;

    public HubSpotSecurityFilter(ObjectMapper objectMapper,
                                 HubSpotCredentials hubSpotCredentials,
                                 @Value("${hubspot.security.timestamp-tolerance-ms:300000}") long timestampToleranceMs,
                                 @Value("${hubspot.security.max-request-size:1048576}") int maxRequestSize) {
        this.objectMapper = objectMapper;
        this.hubSpotCredentials = hubSpotCredentials;
        this.timestampToleranceMs = timestampToleranceMs;
        this.maxRequestSize = maxRequestSize;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);

        try {
            CachedBodyHttpServletRequestWrapper cachedRequest = new CachedBodyHttpServletRequestWrapper(request, maxRequestSize);
            logger.debug("Processing request: method={}, uri={}", cachedRequest.getMethod(), cachedRequest.getRequestURI());
            // Get headers
            String signature = cachedRequest.getHeader("X-HubSpot-Signature-v3");
            String timestamp = cachedRequest.getHeader("X-HubSpot-Request-Timestamp");
            logger.debug("signature: {}, timestamp: {}", signature, timestamp);

            // Deny if missing
            if (signature == null || timestamp == null) {
                logger.warn("Missing required headers for request: {}", cachedRequest.getRequestURI());
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Missing signature or timestamp header");
                return;
            }

            // Deny timestamp older than five minutes, as recommended in HubSpot docs
            try {
                long requestTime = Long.parseLong(timestamp);
                long currentTime = System.currentTimeMillis();
                if (Math.abs(currentTime - requestTime) > timestampToleranceMs) {
                    logger.warn("Timestamp too old for request: {}", cachedRequest.getRequestURI());
                    sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Timestamp is outside allowed range");
                    return;
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid timestamp format: {}", timestamp);
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid timestamp format");
                return;
            }

            // Deny invalid signature
            String method = cachedRequest.getMethod();
            String uri = "https://" + cachedRequest.getServerName() + cachedRequest.getRequestURI();
            String rawBody = new String(cachedRequest.getCachedBodyAsByteArray(), StandardCharsets.UTF_8);
            String body = minifyRawBody(rawBody);
            logger.debug("body: {}", body);
            String source = method + uri + body + timestamp;
            logger.debug("source: {}", source);
            String computedSignature = generateSignature(source, hubSpotCredentials.getClientSecret());
            if (!computedSignature.equals(signature)) {
                logger.warn("Invalid signature for request: {}", cachedRequest.getRequestURI());
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid HubSpot signature");
                return;
            }

            logger.debug("Request validated successfully for uri: {}", cachedRequest.getRequestURI());
            filterChain.doFilter(cachedRequest, response);
        } catch (IOException e) {
            if (e.getMessage().contains("Request body exceeds maximum allowed size")) {
                logger.warn("Oversized request body for request: {}", request.getRequestURI());
                sendError(response, HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, e.getMessage());
            } else {
                logger.error("IO error during validation", e);
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request data");
            }
        } catch (Exception e) {
            logger.error("Unexpected error during validation", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        } finally {
            MDC.clear();
        }
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setContentType("application/json");
        response.setStatus(status);
        objectMapper.writeValue(response.getWriter(), new ErrorResponse(message));
    }

    private String minifyRawBody(String rawBody) {
        try {
            var mapper = new ObjectMapper();
            var jsonNode = mapper.readTree(rawBody);
            return mapper.writeValueAsString(jsonNode);
        } catch (Exception e) {
            return rawBody;
        }
    }
}