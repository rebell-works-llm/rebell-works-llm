package com.rebellworksllm.backend.hubspot.presentation.dto;

public record StudentContact(

        String fullName,
        String email,
        String phoneNumber,
        String study,
        String text,
        String studyLocation,
        String expectedGraduationDate
) {

}
