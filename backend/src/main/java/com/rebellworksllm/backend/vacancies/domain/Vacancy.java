package com.rebellworksllm.backend.vacancies.domain;

import com.rebellworksllm.backend.openai.domain.EmbeddingResult;

public record Vacancy(

        String id,
        String title,
        String description,
        String salary,
        String workingHours,
        String function,
        EmbeddingResult embeddingResult
) {
}