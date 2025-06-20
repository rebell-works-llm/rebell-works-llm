package com.rebellworksllm.backend.matching.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SupabaseUpdateRequest(
        @JsonProperty("match_count") int matchCount
) {
}
