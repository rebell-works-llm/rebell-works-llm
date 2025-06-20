package com.rebellworksllm.backend.vacancies.domain;

public record ScoredVacancy(

        Vacancy vacancy,
        double similarityScore
) {
}