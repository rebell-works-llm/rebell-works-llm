package com.rebellworksllm.backend.embedding.domain;

import com.rebellworksllm.backend.embedding.application.exception.TextEmbeddingException;

public interface TextEmbedder {

    float[] embedText(String text) throws TextEmbeddingException;
}
