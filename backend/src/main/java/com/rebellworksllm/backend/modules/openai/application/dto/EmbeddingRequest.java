package com.rebellworksllm.backend.modules.openai.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EmbeddingRequest(

        @JsonProperty("model")
        String model,

        @JsonProperty("input")
        String input,

        @JsonProperty("encoding_format")
        String encodingFormat
) {
}