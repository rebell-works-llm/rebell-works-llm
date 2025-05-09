package com.rebellworksllm.backend.matching.presentation;

public record HubSpotWebhookPayload(

        long appId,
        long eventId,
        long subscriptionId,
        long portalId,
        long occurredAt, // Instant or LocalDateTime?
        String subscriptionType,
        int attemptNumber,
        long objectId,
        String changeSource,
        String changeFlag
) {

}