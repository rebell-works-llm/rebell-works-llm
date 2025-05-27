package com.rebellworksllm.backend.matching.domain;

import com.rebellworksllm.backend.openai.domain.EmbeddingResult;

public record Student(

        String name,
        String email,
        String phoneNumber,
        String study,
        String lookingForText,
        String studyLocation,
        EmbeddingResult embeddingResult
) {

}