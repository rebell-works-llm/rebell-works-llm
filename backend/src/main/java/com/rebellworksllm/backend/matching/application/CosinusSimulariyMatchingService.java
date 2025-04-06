package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.domain.MatchingService;
import com.rebellworksllm.backend.matching.domain.StudentVacancyMatch;
import com.rebellworksllm.backend.matching.domain.Vacancy;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


//https://www.youtube.com/watch?v=e9U0QAFbfLI
//https://medium.com/advanced-deep-learning/understanding-vector-similarity-b9c10f7506de#:~:text=Cosine%20Similarity%20only%20considers%20the,the%20product%20of%20their%20lengths.
//https://www.restack.io/p/similarity-search-answer-cosine-similarity-java-cat-ai

@Service
public class CosinusSimulariyMatchingService implements MatchingService {

    @Override
    public List<StudentVacancyMatch> findBestMatches(float[] studentQueryVector, List<Vacancy> vacancies, int numOfResults) {
        return vacancies.stream()
                .map(vacancy -> new StudentVacancyMatch(
                        vacancy,
                        studentQueryVector,
                        cosineSimilarity(studentQueryVector, vacancy.getVectors())))
                .sorted(Comparator.comparingDouble(StudentVacancyMatch::matchScore).reversed())
                .limit(numOfResults)
                .collect(Collectors.toList());
    }

    private Double cosineSimilarity(final float[] vec1, final float[] vec2) {
        System.out.println(Arrays.toString(vec1) + " " + Arrays.toString(vec2));
        if (vec1.length != vec2.length) {
            throw new IllegalArgumentException("Vectors must have the same length");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            normA += Math.pow(vec1[i], 2);
            normB += Math.pow(vec2[i], 2);
        }

        double magnitude = Math.sqrt(normA) * Math.sqrt(normB);
        if (magnitude == 0.0) return 0.0;

        return dotProduct / magnitude;
    }
}
