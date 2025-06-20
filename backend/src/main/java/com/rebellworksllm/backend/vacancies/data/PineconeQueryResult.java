package com.rebellworksllm.backend.vacancies.data;

import java.util.List;

public record PineconeQueryResult(

        List<PineconeMatchResponse> matches
) {
}
