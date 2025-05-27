package com.rebellworksllm.backend.hubspot.domain;

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
