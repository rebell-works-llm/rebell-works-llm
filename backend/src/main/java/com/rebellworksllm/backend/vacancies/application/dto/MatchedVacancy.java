package com.rebellworksllm.backend.vacancies.application.dto;

import com.rebellworksllm.backend.openai.domain.EmbeddingResult;

public record MatchedVacancy(

        VacancyResponseDto vacancyResponse,
        EmbeddingResult embeddingResult,
        double similarityScore
) {
}