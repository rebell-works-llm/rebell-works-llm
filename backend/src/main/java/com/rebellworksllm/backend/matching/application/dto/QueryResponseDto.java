package com.rebellworksllm.backend.matching.application.dto;

import java.util.List;

public record QueryResponseDto(
        List<String> foundMatches
) {

    public static QueryResponseDto fromVacancy(List<String> foundMatches) {
        return new QueryResponseDto(foundMatches);
    }
}
