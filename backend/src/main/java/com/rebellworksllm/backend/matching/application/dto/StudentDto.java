package com.rebellworksllm.backend.matching.application.dto;

public record StudentDto(

        String fullName,
        String email,
        String phoneNumber,
        String study,
        String text,
        String studyLocation,
        String expectedGraduationDate
) {

}
