package com.rebellworksllm.backend.matching.application.util;

public class ScoreService {

    private ScoreService() {
    }

    public static double priorityScore(double initialMatchScore, double priority, int matchCount) {
        double priorityScore = initialMatchScore + priority;
        if (matchCount < 2) {
            priorityScore += (0.5 - matchCount * 0.25);
        }
        return priorityScore;
    }
}
