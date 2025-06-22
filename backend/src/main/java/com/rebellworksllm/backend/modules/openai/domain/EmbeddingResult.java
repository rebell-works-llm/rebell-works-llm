package com.rebellworksllm.backend.modules.openai.domain;

import java.util.List;

public record EmbeddingResult(

        List<Double> embeddings
) {
}
