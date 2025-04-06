package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.domain.Student;
import com.rebellworksllm.backend.matching.domain.StudentJobMatchingService;
import com.rebellworksllm.backend.matching.domain.StudentVacancyMatch;
import com.rebellworksllm.backend.matching.domain.Vacancy;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


//https://www.youtube.com/watch?v=e9U0QAFbfLI
//https://medium.com/advanced-deep-learning/understanding-vector-similarity-b9c10f7506de#:~:text=Cosine%20Similarity%20only%20considers%20the,the%20product%20of%20their%20lengths.
//https://www.restack.io/p/similarity-search-answer-cosine-similarity-java-cat-ai

@Service
public class CosSimStudentJobMatchingService implements StudentJobMatchingService {

    @Override
    public List<StudentVacancyMatch> findBestMatches(Student student, List<Vacancy> vacancies, int numOfResults) {
        return vacancies.stream()
                .map(vacancy -> new StudentVacancyMatch(
                        vacancy,
                        student,
                        cosineSimilarity(student.vector(), vacancy.vector())))
                .sorted(Comparator.comparingDouble(StudentVacancyMatch::matchScore).reversed())
                .limit(numOfResults)
                .collect(Collectors.toList());
    }

    private Double cosineSimilarity(final List<Double> vec1, final List<Double> vec2) {
        if (vec1.size() != vec2.size()) {
            throw new IllegalArgumentException("Vectors must have the same length");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vec1.size(); i++) {
            dotProduct += vec1.get(i) * vec2.get(i);
            normA += Math.pow(vec1.get(i), 2);
            normB += Math.pow(vec2.get(i), 2);
        }

        double magnitude = Math.sqrt(normA) * Math.sqrt(normB);
        if (magnitude == 0.0) return 0.0;

        return dotProduct / magnitude;
    }
}
