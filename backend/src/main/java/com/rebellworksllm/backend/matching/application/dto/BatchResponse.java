package com.rebellworksllm.backend.matching.application.dto;

import org.springframework.http.HttpStatus;

import java.util.List;

public record BatchResponse(

        int status,
        String message,
        String batchId,
        List<BatchPayloadResponse> payloadResponses
) {

    public record BatchPayloadResponse(

            long objectId,
            String message
    ) {

    }

    public static BatchResponse success(String batchId, List<BatchPayloadResponse> batchPayloadResponses) {
        return new BatchResponse(
                HttpStatus.OK.value(),
                "Batch processed successfully",
                batchId,
                batchPayloadResponses
        );
    }

    public static BatchResponse failure(String batchId, List<BatchPayloadResponse> batchPayloadResponses) {
        return new BatchResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Batch processing failed",
                batchId,
                batchPayloadResponses
        );
    }
}
