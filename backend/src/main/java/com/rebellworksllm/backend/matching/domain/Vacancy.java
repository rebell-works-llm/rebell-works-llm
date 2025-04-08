package com.rebellworksllm.backend.matching.domain;

import java.util.List;

public record Vacancy(String title, List<Double> vector) {
}
