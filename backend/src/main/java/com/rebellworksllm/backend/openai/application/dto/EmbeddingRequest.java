package com.rebellworksllm.backend.openai.application.dto;

public record EmbeddingRequest(

        String model,
        String input,
        String encoding_format
) {
}