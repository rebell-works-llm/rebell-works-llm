package com.rebellworksllm.backend.modules.matching.domain;

public record StudentVacancyMatch(

        Vacancy vacancy,
        Student student,
        double matchScore
) {
}
