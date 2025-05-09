package com.rebellworksllm.backend.matching.application.dto;

import java.util.Map;

public record ContactResponse(
        String id,
        Map<String, String> properties,
        String createdAt,
        String updatedAt,
        boolean archived
) {
}
