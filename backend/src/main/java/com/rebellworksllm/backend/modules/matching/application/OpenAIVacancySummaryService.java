package com.rebellworksllm.backend.modules.matching.application;


public interface OpenAIVacancySummaryService {

    String generateSummary(String vacancyDescription);
}
