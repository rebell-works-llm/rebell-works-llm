package com.rebellworksllm.backend.modules.openai.application.dto;

import java.util.List;

public record EmbeddingResult(

        List<Double> embeddings
) {
}
