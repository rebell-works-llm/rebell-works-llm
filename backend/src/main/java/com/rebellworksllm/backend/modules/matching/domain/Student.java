package com.rebellworksllm.backend.modules.matching.domain;

import com.rebellworksllm.backend.modules.openai.application.dto.EmbeddingResult;

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