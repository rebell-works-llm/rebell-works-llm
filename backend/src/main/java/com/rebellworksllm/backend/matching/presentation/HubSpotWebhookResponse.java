package com.rebellworksllm.backend.matching.presentation;

public record HubSpotWebhookResponse(

        int statusCode,
        String message
) {
}
