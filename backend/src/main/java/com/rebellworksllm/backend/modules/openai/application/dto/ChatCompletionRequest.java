package com.rebellworksllm.backend.modules.openai.application.dto;

import java.util.List;

public record ChatCompletionRequest(
        String model,
        List<ChatMessage> messages,
        double temperature
) {
}
