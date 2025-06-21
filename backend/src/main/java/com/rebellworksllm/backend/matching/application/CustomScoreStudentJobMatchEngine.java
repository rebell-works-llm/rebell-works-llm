package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.application.exception.MatchingException;
import com.rebellworksllm.backend.matching.application.util.ScoreService;
import com.rebellworksllm.backend.matching.domain.Student;
import com.rebellworksllm.backend.matching.domain.StudentVacancyMatch;
import com.rebellworksllm.backend.matching.domain.Vacancy;
import com.rebellworksllm.backend.vacancies.application.VacancyProvider;
import com.rebellworksllm.backend.vacancies.application.dto.MatchedVacancy;
import com.rebellworksllm.backend.vacancies.application.dto.VacancyResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class CustomScoreStudentJobMatchEngine implements StudentJobMatchEngine {

    private static final Logger logger = LoggerFactory.getLogger(HubSpotWebhookService.class);
    private static final int TOP_K = 50;

    private final VacancyProvider vacancyProvider;

    public CustomScoreStudentJobMatchEngine(VacancyProvider vacancyProvider) {
        this.vacancyProvider = vacancyProvider;
    }

    @Override
    public List<StudentVacancyMatch> query(Student student, int amount) {
        logger.info("Starting match engine for student with topK: {}, amount: {}", TOP_K, amount);
        try {
            List<Double> studentVector = student.embeddingResult().embeddings();
            List<MatchedVacancy> matchedVacancies = vacancyProvider.getVacanciesBySimilarity(studentVector, TOP_K);
            List<StudentVacancyMatch> initialMatches = matchedVacancies.stream()
                    .map(scoredVacancy -> new StudentVacancyMatch(
                            new Vacancy(
                                    scoredVacancy.vacancyResponse().id(),
                                    scoredVacancy.vacancyResponse().title(),
                                    scoredVacancy.vacancyResponse().description(),
                                    scoredVacancy.vacancyResponse().salary(),
                                    scoredVacancy.vacancyResponse().workingHours(),
                                    scoredVacancy.vacancyResponse().function(),
                                    scoredVacancy.embeddingResult()
                            ),
                            student,
                            scoredVacancy.similarityScore()
                    ))
                    .toList();

            return initialMatches.stream()
                    .map(match -> {
                        VacancyResponseDto response = vacancyProvider.getVacancyById(match.vacancy().id());
                        double newScore = ScoreService.priorityScore(match.matchScore(), response.priority(), response.matchCount());
                        return new StudentVacancyMatch(
                                match.vacancy(),
                                match.student(),
                                newScore
                        );
                    })
                    .sorted(Comparator.comparingDouble(StudentVacancyMatch::matchScore).reversed())
                    .limit(amount)
                    .toList();
        } catch (Exception e) {
            logger.error("Failed to query matches from Pinecone for student: {}, error: {}", student.name(), e.getMessage(), e);
            throw new MatchingException("Pinecone matching failed", e);
        }
    }
}
