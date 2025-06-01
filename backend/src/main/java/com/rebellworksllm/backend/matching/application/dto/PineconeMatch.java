package com.rebellworksllm.backend.matching.application.dto;

import java.util.List;

public record PineconeMatch(

        String id,
        double score,
        List<Double> values,
        PineconeMetadata metadata
) {

}
