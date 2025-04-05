package com.rebellworksllm.backend.embedding.application.exception;

public class TextEmbeddingException extends RuntimeException{

    public TextEmbeddingException(String message) {
        super(message);
    }
}
