package com.rebellworksllm.backend.whatsapp.presentation.dto;

import java.util.List;

public record WhatsAppWebhookPayload(
        String object,
        List<Entry> entry
) {
    public record Entry(
            String id,
            List<Change> changes
    ) {
        public record Change(
                String field,
                Value value
        ) {
            public record Value(
                    String messagingProduct,
                    List<Contact> contacts,
                    List<Message> messages
            ) {
                public record Contact(String waId, String profileName) {
                }

                public record Message(String from, String id, String timestamp, Button button) {
                    public record Button(String payload, String text) {
                    }
                }
            }
        }
    }
}
