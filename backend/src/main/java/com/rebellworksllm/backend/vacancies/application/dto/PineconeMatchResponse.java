package com.rebellworksllm.backend.vacancies.application.dto;

import java.util.List;

public record PineconeMatchResponse(

        String id,
        double score,
        List<Double> values,
        PineconeMetadata metadata
) {

    public record PineconeMetadata(

            String title,
            String description,
            String working_hours,
            String salary,
            String position



    ) {
    }
}
