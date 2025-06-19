package com.rebellworksllm.backend.vacancies.application.dto;

import java.time.LocalDateTime;
import java.util.List;

public record SupabaseResponse(
        String id,
        String title,
        String location,
        String description,
        int salary,
        List<String> tags,
        String link,
        int workingHours,
        String position,
        String employment,
        double priority,
        int match_count,
        LocalDateTime publicationDate,
        LocalDateTime syncedToPinecone
) {
}
