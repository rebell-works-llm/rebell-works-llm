package com.rebellworksllm.backend.openai.application;

import com.rebellworksllm.backend.openai.presentation.dto.EmbeddingResult;

public interface OpenAIEmbeddingService {

    EmbeddingResult embedText(String text);
}
