package com.rebellworksllm.backend.modules.matching.application.dto;

import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.UUID;

public record BatchResponse(
        int status,
        String message,
        String batchId,
        Instant timestamp
) {

    public static BatchResponse accepted() {
        return new BatchResponse(
                HttpStatus.ACCEPTED.value(),
                "Batch accepted for processing",
                UUID.randomUUID().toString(),
                Instant.now()
        );
    }

    public static BatchResponse failed() {
        return new BatchResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Failed to start batch processing",
                UUID.randomUUID().toString(),
                Instant.now()
        );
    }
}
