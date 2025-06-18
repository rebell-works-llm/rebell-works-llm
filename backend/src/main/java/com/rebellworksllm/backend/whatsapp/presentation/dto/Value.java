package com.rebellworksllm.backend.whatsapp.presentation.dto;

import java.util.List;

public record Value(
        String messaging_product,
        Metadata metadata,
        List<Contact> contacts,
        List<Message> messages
) {
}