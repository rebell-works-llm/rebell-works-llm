package com.rebellworksllm.backend.matching.domain;

import java.util.List;

public record Student(String query, List<Double> vector) {
}