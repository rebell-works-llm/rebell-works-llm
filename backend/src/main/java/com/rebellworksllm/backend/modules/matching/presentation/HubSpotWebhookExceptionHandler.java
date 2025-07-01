package com.rebellworksllm.backend.modules.matching.presentation;

import com.rebellworksllm.backend.modules.hubspot.application.exception.HubSpotStudentNotFoundException;
import com.rebellworksllm.backend.modules.matching.application.exception.InsufficientMatchesException;
import com.rebellworksllm.backend.modules.matching.application.exception.MatchingException;
import com.rebellworksllm.backend.modules.matching.presentation.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = {HubSpotWebhookController.class})
public class HubSpotWebhookExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(HubSpotWebhookExceptionHandler.class);

    @ExceptionHandler(HubSpotStudentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStudentNotFoundException(HubSpotStudentNotFoundException e) {
        logger.warn("Student not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("HubSpot contact not found: " + e.getMessage(), "STUDENT_NOT_FOUND"));
    }

    @ExceptionHandler(InsufficientMatchesException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientMatchesException(InsufficientMatchesException e) {
        logger.warn("Insufficient matches: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Insufficient vacancy matches: " + e.getMessage(), "INSUFFICIENT_MATCHES"));
    }

    @ExceptionHandler(MatchingException.class)
    public ResponseEntity<ErrorResponse> handleMatchingException(MatchingException e) {
        logger.warn("Matching exception: message={}, cause={}", e.getMessage(), e.getCause() != null ? e.getCause().getMessage() : "none");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Matching failed: " + e.getMessage(), "MATCHING_FAILED"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        logger.error("Unexpected error: message={}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("An unexpected error occurred", "UNEXPECTED_ERROR"));
    }
}
