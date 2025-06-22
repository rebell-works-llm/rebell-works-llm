package com.rebellworksllm.backend.modules.openai.domain;

public interface OpenAIEmbeddingService {

    EmbeddingResult embedText(String text);
}
