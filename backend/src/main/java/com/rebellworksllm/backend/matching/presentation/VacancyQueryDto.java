package com.rebellworksllm.backend.matching.presentation;

public record VacancyQueryDto(

        String study,
        String messageText,
        String location,
        String phoneNumber
) {
}
