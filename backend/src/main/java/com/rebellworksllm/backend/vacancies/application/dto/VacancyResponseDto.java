package com.rebellworksllm.backend.vacancies.application.dto;

import com.rebellworksllm.backend.vacancies.domain.Vacancy;

public record VacancyResponseDto(

        String id,
        String title,
        String description,
        String salary,
        String workingHours,
        String link,
        String function,
        double priority,
        int matchCount
) {

    public static VacancyResponseDto from(Vacancy vacancy) {
        return new VacancyResponseDto(
                vacancy.getId(),
                vacancy.getTitle(),
                vacancy.getDescription(),
                vacancy.getSalary(),
                vacancy.getWorkingHours(),
                vacancy.getLink(),
                vacancy.getFunction(),
                vacancy.getPriority(),
                vacancy.getMatchCount()
        );
    }
}