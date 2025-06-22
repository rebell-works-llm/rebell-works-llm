package com.rebellworksllm.backend.matching.application.dto;

import org.springframework.http.HttpStatus;

import java.util.List;

public record BatchResponse(

        int status,
        String message,
        String batchId,
        List<BatchPayloadResponse> payloadResponses
) {

    public record BatchPayloadStepResult(
            String step,
            boolean success,
            String message
    ) {}

    public record BatchPayloadResponse(
            long objectId,
            List<BatchPayloadStepResult> steps
    ) {}

    public static BatchPayloadResponse singleMessage(long objectId, String message) {
        return new BatchPayloadResponse(
                objectId,
                List.of(new BatchPayloadStepResult("result", message == null, message))
        );
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
