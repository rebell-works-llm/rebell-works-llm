package com.rebellworksllm.backend.whatsapp.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record WhatsAppWebhookPayload(
        @NotNull
        @JsonProperty("field")
        String field,

        @NotNull
        @JsonProperty("value")
        Value value
) {

}
