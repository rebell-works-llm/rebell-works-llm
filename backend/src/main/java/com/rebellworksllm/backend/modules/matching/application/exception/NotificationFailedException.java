package com.rebellworksllm.backend.modules.matching.application.exception;

public class NotificationFailedException extends WorkflowException {

    public NotificationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}