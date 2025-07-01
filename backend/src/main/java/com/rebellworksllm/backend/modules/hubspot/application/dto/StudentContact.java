package com.rebellworksllm.backend.modules.hubspot.application.dto;

public record StudentContact(

        String id,
        String fullName,
        String email,
        String phoneNumber,
        String study,
        String text,
        String studyLocation,
        String expectedGraduationDate
) {

    public String stringify() {
        return study + " " + text + " " + studyLocation;
    }
}
