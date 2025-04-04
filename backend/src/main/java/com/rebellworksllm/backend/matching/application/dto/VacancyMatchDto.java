package com.rebellworksllm.backend.matching.application.dto;

public record VacancyMatchDto(
        String queryResponse
) {

    public static VacancyMatchDto fromVacancy(String vacancy) {
        return new VacancyMatchDto(vacancy);
    }
}
