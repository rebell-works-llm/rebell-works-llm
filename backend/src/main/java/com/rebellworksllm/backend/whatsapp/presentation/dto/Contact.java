package com.rebellworksllm.backend.whatsapp.presentation.dto;

public record Contact(
        Profile profile,
        String wa_id
) {
}