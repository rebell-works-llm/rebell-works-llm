package com.rebellworksllm.backend.matching.presentation.dto;

import java.time.Instant;

public record ErrorResponse(
        String errorMessage,
        String errorCode,
        Instant timestamp
) {
    public ErrorResponse(String errorMessage) {
        this(errorMessage, "UNKNOWN", Instant.now());
    }

    public ErrorResponse(String errorMessage, String errorCode) {
        this(errorMessage, errorCode, Instant.now());
    }
}