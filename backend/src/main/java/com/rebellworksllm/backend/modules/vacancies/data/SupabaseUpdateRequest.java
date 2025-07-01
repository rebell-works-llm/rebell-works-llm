package com.rebellworksllm.backend.modules.vacancies.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SupabaseUpdateRequest(
        @JsonProperty("match_count") int matchCount
) {
}
