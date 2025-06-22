package com.rebellworksllm.backend.modules.matching.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public record MatchMessageResponse(

        String id,
        @JsonProperty("vacancy_ids") List<String> vacancyIds,
        String contactPhone,
        Instant sent_at
) {
}
