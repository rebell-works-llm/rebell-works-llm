package com.rebellworksllm.backend.matching.application.exception;

public class MatchingException extends RuntimeException {

    public MatchingException(String message) {
        super(message);
    }

    public MatchingException(String message, Throwable cause) {
        super(message);
    }
}
