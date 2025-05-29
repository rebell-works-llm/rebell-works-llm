package com.rebellworksllm.backend.matching.application.dto;

import java.util.List;

public record PineconeQueryResult(

        List<PineconeMatch> matches
) {
}
