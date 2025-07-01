package com.rebellworksllm.backend.modules.matching.application.exception;

public class InsufficientMatchesException extends RuntimeException {

    public InsufficientMatchesException(String message) {
        super(message);
    }

    public InsufficientMatchesException(String message, Throwable cause) {
        super(message);
    }
}
