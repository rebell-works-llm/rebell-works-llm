package com.rebellworksllm.backend.openai.domain;

public interface OpenAIEmbeddingService {

    EmbeddingResult embedText(String text);
}
