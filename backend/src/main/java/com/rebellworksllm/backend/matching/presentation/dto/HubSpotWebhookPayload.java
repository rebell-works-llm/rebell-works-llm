package com.rebellworksllm.backend.matching.presentation.dto;

public record HubSpotWebhookPayload(
        long objectId,
        String subscriptionType
) {
}