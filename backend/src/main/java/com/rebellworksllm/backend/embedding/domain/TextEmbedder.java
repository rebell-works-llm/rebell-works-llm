package com.rebellworksllm.backend.embedding.domain;

public interface TextEmbedder {

    Vectors embedText(String text);
}
