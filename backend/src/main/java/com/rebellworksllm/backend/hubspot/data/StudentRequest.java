package com.rebellworksllm.backend.hubspot.data;

import java.util.Map;

public record StudentRequest(
        String id,
        Map<String, String> properties,
        String createdAt,
        String updatedAt,
        boolean archived
) {

}
