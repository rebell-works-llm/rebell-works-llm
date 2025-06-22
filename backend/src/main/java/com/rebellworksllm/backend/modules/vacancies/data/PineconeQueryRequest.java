package com.rebellworksllm.backend.modules.vacancies.data;

import java.util.List;

public record PineconeQueryRequest(

        List<Double> vector,
        int topK,
        boolean includeValues,
        boolean includeMetadata
) {


}
