package com.rebellworksllm.backend.whatsapp.presentation.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record WhatsAppMessagesData(

        @NotNull String field,
        @NotNull Value value
) {

    public record Value(
            String messaging_product,
            Metadata metadata,
            List<Contact> contacts,
            List<Message> messages
    ) {
        public record Metadata(
                String display_phone_number,
                String phone_number_id
        ) {
        }

        public record Contact(
                Profile profile,
                String wa_id
        ) {

            public record Profile(
                    String name
            ) {}
        }

        public record Message(
                String from,
                String id,
                String timestamp,
                String type,
                Text text
        ) {

            public record Text(
                    String body
            ) {
            }
        }
    }
}
