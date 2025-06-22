package com.rebellworksllm.backend.modules.openai.application.dto;


public record Usage(
        int prompt_tokens,
        int total_tokens
) {
}