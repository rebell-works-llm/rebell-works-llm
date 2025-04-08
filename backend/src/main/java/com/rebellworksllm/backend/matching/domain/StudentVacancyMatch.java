package com.rebellworksllm.backend.matching.domain;

public record StudentVacancyMatch(Vacancy vacancy, Student student, double matchScore) {
}
