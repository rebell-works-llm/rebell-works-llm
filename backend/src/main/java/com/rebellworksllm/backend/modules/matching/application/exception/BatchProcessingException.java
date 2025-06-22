package com.rebellworksllm.backend.modules.matching.application.exception;

public class BatchProcessingException extends RuntimeException {

    public BatchProcessingException(String message) {
        super(message);
    }

    public BatchProcessingException(String message, Throwable cause) {
        super(message);
    }
}
