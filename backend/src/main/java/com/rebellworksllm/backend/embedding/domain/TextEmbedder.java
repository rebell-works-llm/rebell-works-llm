package com.rebellworksllm.backend.embedding.domain;

import java.util.List;

public interface TextEmbedder {

    List<Double> embedText(String text);
}
