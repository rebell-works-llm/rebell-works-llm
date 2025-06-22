package com.rebellworksllm.backend.modules.vacancies.data;


import com.fasterxml.jackson.annotation.JsonProperty;

public record SupabaseResponse(
        String id,
        double priority,
        @JsonProperty("match_count") int matchCount,
        String link,
        String title
) {
}
