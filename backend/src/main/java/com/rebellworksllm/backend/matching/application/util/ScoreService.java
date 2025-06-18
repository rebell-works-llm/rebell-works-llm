package com.rebellworksllm.backend.matching.application.util;

public class ScoreService {

    private ScoreService() {
    }


    public static double priorityScore(double initialMatchScore, double priority, int matchCount) {
        double priorityScore = initialMatchScore + priority;
        if (matchCount < 3) {
            priorityScore += (0.6 - matchCount * 0.2);
        }
        return priorityScore;
    }
}
