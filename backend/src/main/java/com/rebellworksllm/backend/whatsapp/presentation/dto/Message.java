package com.rebellworksllm.backend.whatsapp.presentation.dto;

public record Message(
        String from,
        String id,
        String timestamp,
        String type,
        Text text
) {
}