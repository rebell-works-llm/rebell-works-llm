package com.rebellworksllm.backend.matching.application.dto;

public record QueryResponseDto(
        String queryResponse
) {

    public static QueryResponseDto fromVacancy(String vacancy) {
        return new QueryResponseDto(vacancy);
    }
}
