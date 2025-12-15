package com.rebellworksllm.backend.modules.matching.application.exception;

public class StudentFetchFailedException extends WorkflowException {

    public StudentFetchFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}