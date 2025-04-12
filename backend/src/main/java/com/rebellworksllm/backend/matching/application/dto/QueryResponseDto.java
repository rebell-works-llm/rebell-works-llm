package com.rebellworksllm.backend.matching.application.dto;

import com.rebellworksllm.backend.matching.domain.Match;

import java.util.List;

public record QueryResponseDto(
        Match bestMatch,
        List<Match> otherMatches
) {

    public static QueryResponseDto fromVacancy(Match bestMatch, List<Match> otherMatches) {
        return new QueryResponseDto(bestMatch, otherMatches);
    }
}
