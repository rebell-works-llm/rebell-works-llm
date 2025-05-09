package com.rebellworksllm.backend.matching.domain;

import com.rebellworksllm.backend.embedding.domain.Vectors;

public record Student(

        String name,
        String email,
        String phoneNumber,
        String study,
        String lookingForText,
        String studyLocation,
        Vectors vectors
) {

}