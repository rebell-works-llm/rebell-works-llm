package com.rebellworksllm.backend.vacancies.application.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

public record SupabaseResponse(
        String id,
        double priority,
        @JsonProperty("match_count") int matchCount
) {
}
