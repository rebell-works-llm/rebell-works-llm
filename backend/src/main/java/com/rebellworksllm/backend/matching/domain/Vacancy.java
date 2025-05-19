package com.rebellworksllm.backend.matching.domain;

import com.rebellworksllm.backend.embedding.domain.Vectors;


public record Vacancy(

        String title,
        String website,
        Vectors vectors,
        double priority,
        int matches
) {
}
