package com.rebellworksllm.backend.modules.vacancies.data;


import com.fasterxml.jackson.annotation.JsonProperty;

public record SupabaseResponse(
        String id,
        String description,
        String salary,
        String workingHours,
        String function,
        double priority,
        @JsonProperty("match_count") int matchCount,
        String link,
        String title
) {
}
