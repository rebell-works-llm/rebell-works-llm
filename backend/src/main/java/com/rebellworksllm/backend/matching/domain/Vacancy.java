package com.rebellworksllm.backend.matching.domain;

import com.rebellworksllm.backend.openai.domain.EmbeddingResult;


public record Vacancy(

        String id,
        String title,
        String website,
        EmbeddingResult embeddingResult
) {
}
