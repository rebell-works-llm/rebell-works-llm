package com.rebellworksllm.backend.modules.openai.application.dto;


import java.util.List;

public record ChatCompletionResponse(
        List<Choice> choices
) {
    public String getFirstMessageContent() {
        return choices.getFirst().message().content();
    }

    public record Choice(ChatMessage message) {
    }
}