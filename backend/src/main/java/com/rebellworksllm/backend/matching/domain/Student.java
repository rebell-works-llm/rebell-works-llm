package com.rebellworksllm.backend.matching.domain;

import com.rebellworksllm.backend.openai.domain.EmbeddingResult;

public record Student(

        String id,
        String name,
        String email,
        String phoneNumber,
        String study,
        String lookingForText,
        String studyLocation,
        EmbeddingResult embeddingResult
) {

}