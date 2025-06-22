package com.rebellworksllm.backend.modules.vacancies.application.dto;

import com.rebellworksllm.backend.modules.openai.application.dto.EmbeddingResult;

public record MatchedVacancy(

        VacancyResponseDto vacancyResponse,
        EmbeddingResult embeddingResult,
        double similarityScore
) {
}