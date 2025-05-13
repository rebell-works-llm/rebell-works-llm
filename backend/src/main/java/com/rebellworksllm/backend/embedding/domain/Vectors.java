package com.rebellworksllm.backend.embedding.domain;

import java.util.List;

public record Vectors(

        List<Double> embeddings
) {
}
