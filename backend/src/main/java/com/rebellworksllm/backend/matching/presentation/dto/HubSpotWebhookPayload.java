package com.rebellworksllm.backend.matching.presentation.dto;

import jakarta.validation.constraints.Positive;

public record HubSpotWebhookPayload(

        long appId,
        long eventId,
        long subscriptionId,
        long portalId,
        long occurredAt, // Instant or LocalDateTime?
        String subscriptionType,
        int attemptNumber,

        @Positive
        long objectId,

        String changeSource,
        String changeFlag
) {

}