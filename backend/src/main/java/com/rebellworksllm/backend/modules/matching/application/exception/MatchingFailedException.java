package com.rebellworksllm.backend.modules.matching.application.exception;

public class MatchingFailedException extends WorkflowException {

    public MatchingFailedException(String message) {
        super(message);
    }

    public MatchingFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}