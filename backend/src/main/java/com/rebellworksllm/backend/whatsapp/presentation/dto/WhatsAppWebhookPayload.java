package com.rebellworksllm.backend.whatsapp.presentation.dto;

import jakarta.validation.constraints.NotNull;

public record WhatsAppWebhookPayload(
        @NotNull String field,
        @NotNull Value value
) {

}
