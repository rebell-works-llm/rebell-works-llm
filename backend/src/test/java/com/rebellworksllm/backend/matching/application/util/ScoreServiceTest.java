package com.rebellworksllm.backend.matching.application.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ScoreServiceTest {

    private ScoreService scoreService;

    @ParameterizedTest
    @MethodSource("provideParameters")
    void testScoreCalculation(double initialScore, double priority, int matchCount, double expectedScore) {
        double priorityScore = ScoreService.priorityScore(initialScore, priority, matchCount);
        assertEquals(expectedScore, priorityScore);
    }
    static Stream<Arguments> provideParameters(){
        return Stream.of(
                Arguments.of(0.8, 0.0, 3, 0.8),
                Arguments.of(0.8, 1.0, 3, 1.8),
                Arguments.of(0.8, 0.0, 0, 1.4),
                Arguments.of(0.8, 0.0, 1, 1.2),
                Arguments.of(0.8, 0.0, 2, 1.0),
                Arguments.of(0.8, 1.0, 2, 2.0),
                Arguments.of(0.8, 1.0, 1, 2.2),
                Arguments.of(0.8, 1.0, 0, 2.4)
        );
    }



}