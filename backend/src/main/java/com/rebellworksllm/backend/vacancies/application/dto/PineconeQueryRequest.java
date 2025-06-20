package com.rebellworksllm.backend.vacancies.application.dto;

import java.util.List;

public record PineconeQueryRequest(

        List<Double> vector,
        int topK,
        boolean includeValues,
        boolean includeMetadata
) {


}
