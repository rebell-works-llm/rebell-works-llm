package com.rebellworksllm.backend.matching.application.dto;

import java.util.List;

public record MatchResponseDto(

        VacancyMatchDto bestMatch,
        List<VacancyMatchDto> otherMatches
) {

    public static MatchResponseDto fromVacancy(VacancyMatchDto bestMatch, List<VacancyMatchDto> otherMatches) {
        return new MatchResponseDto(bestMatch, otherMatches);
    }
}
