package com.rebellworksllm.backend.matching.presentation;

import com.rebellworksllm.backend.matching.application.exception.MatchingException;
import com.rebellworksllm.backend.matching.presentation.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = {HubSpotWebhookController.class})
public class HubSpotWebhookExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(HubSpotWebhookExceptionHandler.class);

    @ExceptionHandler(MatchingException.class)
    public ResponseEntity<ErrorResponse> handleMatchingException(MatchingException e) {
        logger.warn("Matching exception occurred: message={}, cause={}", e.getMessage(), e.getCause() != null ? e.getCause().getMessage() : "none");
        ErrorResponse errorResponse = new ErrorResponse("Matching failed: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        logger.error("Unexpected error occurred: message={}", e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse("An unexpected error occurred: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
