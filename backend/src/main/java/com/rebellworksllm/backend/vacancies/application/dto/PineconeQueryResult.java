package com.rebellworksllm.backend.vacancies.application.dto;

import java.util.List;

public record PineconeQueryResult(

        List<PineconeMatchResponse> matches
) {
}
