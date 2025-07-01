package com.rebellworksllm.backend.modules.openai.application.dto;

public record EmbeddingRequest(

        String model,
        String input,
        String encoding_format
) {
}