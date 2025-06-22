package com.rebellworksllm.backend.modules.matching.application.exception;

public class TemplateException extends RuntimeException {

    public TemplateException(String message) {
        super(message);
    }

    public TemplateException(String message, Throwable cause) {
        super(message);
    }
}
