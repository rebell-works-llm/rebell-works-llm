package com.rebellworksllm.backend.openai.domain;

import java.util.List;

public record EmbeddingResult(

        List<Double> embeddings
) {
}
