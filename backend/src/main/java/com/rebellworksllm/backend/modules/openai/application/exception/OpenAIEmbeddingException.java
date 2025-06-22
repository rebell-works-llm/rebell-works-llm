package com.rebellworksllm.backend.modules.openai.application.exception;

public class OpenAIEmbeddingException extends RuntimeException {

    public OpenAIEmbeddingException(String message) {
        super(message);
    }

    public OpenAIEmbeddingException(String message, Throwable cause) {
        super(message, cause);
    }
}
