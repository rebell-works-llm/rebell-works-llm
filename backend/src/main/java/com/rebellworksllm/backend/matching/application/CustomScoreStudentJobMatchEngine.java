package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.application.exception.MatchingException;
import com.rebellworksllm.backend.matching.domain.Student;
import com.rebellworksllm.backend.matching.domain.StudentVacancyMatch;
import com.rebellworksllm.backend.matching.domain.Vacancy;
import com.rebellworksllm.backend.vacancies.application.VacancyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomScoreStudentJobMatchEngine implements StudentJobMatchEngine {

    private static final Logger logger = LoggerFactory.getLogger(HubSpotWebhookService.class);

    private final VacancyProvider vacancyProvider;

    public CustomScoreStudentJobMatchEngine(VacancyProvider vacancyProvider) {
        this.vacancyProvider = vacancyProvider;
    }

    @Override
    public List<StudentVacancyMatch> query(Student student, int amount) {
        logger.info("Starting match engine for student, topK: {}", amount);
        try {
            return vacancyProvider.getVacanciesBySimilarity(student.embeddingResult().embeddings(), amount).stream()
                    .map(scoredVacancy -> new StudentVacancyMatch(
                            new Vacancy(
                                    scoredVacancy.vacancy().id(),
                                    scoredVacancy.vacancy().title(),
                                    scoredVacancy.vacancy().website()
                            ),
                            student,
                            scoredVacancy.similarityScore()
                    ))
                    .toList();
        } catch (Exception e) {
            logger.error("Failed to query matches from Pinecone for student: {}, error: {}", student.name(), e.getMessage(), e);
            throw new MatchingException("Pinecone matching failed", e);
        }
    }
}
