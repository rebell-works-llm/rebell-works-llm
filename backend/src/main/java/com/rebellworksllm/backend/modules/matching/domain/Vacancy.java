package com.rebellworksllm.backend.modules.matching.domain;


public record Vacancy(

        String id,
        String title,
        String description,
        String salary,
        String workingHours,
        String function
) {
}