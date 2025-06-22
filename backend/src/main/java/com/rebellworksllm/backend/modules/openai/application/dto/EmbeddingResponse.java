package com.rebellworksllm.backend.modules.openai.application.dto;

import java.util.List;

public record EmbeddingResponse(
        String object,
        List<EmbeddingData> data,
        String model,
        Usage usage
) {
}
