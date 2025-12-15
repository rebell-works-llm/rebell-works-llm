package com.rebellworksllm.backend.modules.matching.application.exception;

public abstract class WorkflowException extends RuntimeException {

    protected WorkflowException(String message) {
        super(message);
    }

    protected WorkflowException(String message, Throwable cause) {
        super(message, cause);
    }
}