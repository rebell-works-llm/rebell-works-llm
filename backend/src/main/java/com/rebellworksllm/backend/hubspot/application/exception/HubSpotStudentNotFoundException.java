package com.rebellworksllm.backend.hubspot.application.exception;

public class HubSpotStudentNotFoundException extends RuntimeException {

    public HubSpotStudentNotFoundException(String message) {
        super(message);
    }
}