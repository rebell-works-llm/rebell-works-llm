package com.rebellworksllm.backend.modules.matching.domain;

import com.rebellworksllm.backend.modules.openai.application.dto.EmbeddingResult;

public record Vacancy(

        String id,
        String title,
        String description,
        String salary,
        String workingHours,
        String function
) {
}