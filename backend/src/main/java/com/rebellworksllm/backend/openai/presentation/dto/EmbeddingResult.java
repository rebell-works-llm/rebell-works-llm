package com.rebellworksllm.backend.openai.presentation.dto;

import java.util.List;

public record EmbeddingResult(

        List<Double> embeddings
) {
}
