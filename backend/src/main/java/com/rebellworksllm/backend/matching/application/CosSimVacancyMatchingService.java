package com.rebellworksllm.backend.matching.application;


import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CosSimVacancyMatchingService implements VacancyMatchingService {

    @Override
    public String findBestMatch(float[] studentVector, List<float[]> vacancyVectors, List<String> titles) {
        if (vacancyVectors.size() != titles.size()) {
            throw new IllegalArgumentException("Each vacancy vector must have a matching title.");
        }

        double bestScore = -1;
        String bestMatch = null;

        for (int i = 0; i < vacancyVectors.size(); i++) {
            float[] vacancyVector = vacancyVectors.get(i);
            double score = cosineSimilarity(studentVector, vacancyVector);

            if (score > bestScore) {
                bestScore = score;
                bestMatch = titles.get(i);
            }
        }

        return bestMatch + " (Score: " + String.format("%.4f", bestScore) + ")";
    }

    private double cosineSimilarity(float[] a, float[] b) {
        if (a.length != b.length) throw new IllegalArgumentException("Vectors must be of same length");

        double dot = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB) + 1e-10);
    }
}
