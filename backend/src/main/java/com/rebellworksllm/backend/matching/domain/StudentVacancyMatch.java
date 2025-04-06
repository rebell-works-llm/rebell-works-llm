package com.rebellworksllm.backend.matching.domain;

public record StudentVacancyMatch(Vacancy vacancy, float[] studentQueryVector, double matchScore) {
}
