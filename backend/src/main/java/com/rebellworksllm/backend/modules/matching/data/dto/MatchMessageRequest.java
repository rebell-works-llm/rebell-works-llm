package com.rebellworksllm.backend.modules.matching.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MatchMessageRequest(
        @JsonProperty("vacancy_ids") List<String> vacancyIds,
        String contactPhone
) {
}
