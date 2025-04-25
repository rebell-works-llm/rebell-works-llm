package com.rebellworksllm.backend.matching.presentation;

public record QueryRequestsDto(

        String name,
        String messageText,
        String phoneNumber
) {
}
