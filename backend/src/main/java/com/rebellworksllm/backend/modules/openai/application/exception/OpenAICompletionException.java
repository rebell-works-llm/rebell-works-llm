package com.rebellworksllm.backend.modules.openai.application.exception;

public class OpenAICompletionException extends RuntimeException {

    public OpenAICompletionException(String message) {
        super(message);
    }

}
