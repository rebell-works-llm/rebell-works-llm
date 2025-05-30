package com.rebellworksllm.backend.matching.presentation.dto;

public record HubSpotWebhookResponse(

        int statusCode,
        String message
) {
}
