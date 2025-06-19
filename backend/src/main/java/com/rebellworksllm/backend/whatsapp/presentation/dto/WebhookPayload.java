package com.rebellworksllm.backend.whatsapp.presentation.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record WebhookPayload(
        @NotNull
        String object,

        List<Entry> entry
) {

    public record Entry(
            String id,
            List<WhatsAppMessagesData> changes
    ) {
    }

}
