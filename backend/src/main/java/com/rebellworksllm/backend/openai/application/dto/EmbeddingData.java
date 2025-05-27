package com.rebellworksllm.backend.openai.application.dto;

import java.util.List;

public record EmbeddingData(
        String object,
        int index,
        List<Double> embedding
) {
}
