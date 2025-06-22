package com.rebellworksllm.backend.modules.vacancies.application.dto;

import com.rebellworksllm.backend.modules.openai.domain.EmbeddingResult;

public record MatchedVacancy(

        VacancyResponseDto vacancyResponse,
        EmbeddingResult embeddingResult,
        double similarityScore
) {
}