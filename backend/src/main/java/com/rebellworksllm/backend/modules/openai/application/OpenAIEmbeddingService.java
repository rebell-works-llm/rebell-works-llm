package com.rebellworksllm.backend.modules.openai.application;

import com.rebellworksllm.backend.modules.openai.application.dto.EmbeddingResult;

public interface OpenAIEmbeddingService {

    EmbeddingResult embedText(String text);
}
