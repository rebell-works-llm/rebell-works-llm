package com.rebellworksllm.backend.modules.vacancies.data;

import java.util.List;

public record PineconeQueryResult(

        List<PineconeMatchResponse> matches
) {
}
