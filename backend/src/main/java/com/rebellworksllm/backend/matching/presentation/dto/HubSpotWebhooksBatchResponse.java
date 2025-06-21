package com.rebellworksllm.backend.matching.presentation.dto;


import jakarta.validation.constraints.NotNull;

import java.util.List;

public record HubSpotWebhooksBatchResponse(

        List<HubSpotWebhooksPayload> payloads
) {

    public record HubSpotWebhooksPayload(

            @NotNull
            long objectId,
            String subscriptionType
    ) {
    }
}