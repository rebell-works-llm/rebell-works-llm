package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.application.dto.PineconeMatch;
import com.rebellworksllm.backend.matching.application.dto.PineconeQueryRequest;
import com.rebellworksllm.backend.matching.application.dto.PineconeQueryResult;
import com.rebellworksllm.backend.matching.application.exception.MatchingException;
import com.rebellworksllm.backend.matching.domain.Student;
import com.rebellworksllm.backend.matching.domain.StudentVacancyMatch;
import com.rebellworksllm.backend.matching.domain.Vacancy;
import com.rebellworksllm.backend.openai.domain.EmbeddingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class PineconeService {

    private static final Logger logger = LoggerFactory.getLogger(PineconeService.class);

    private final RestTemplate restTemplate;

    public PineconeService(@Qualifier("pineconeRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<StudentVacancyMatch> queryTopMatches(Student student, int totalMatches) {
        logger.debug("Querying {} Pinecone matches for student: {}", student.name(), totalMatches);

        try {
            PineconeQueryRequest request = new PineconeQueryRequest(
                    student.embeddingResult().embeddings(), totalMatches, true, true);
            HttpEntity<PineconeQueryRequest> entity = new HttpEntity<>(request);

            ResponseEntity<PineconeQueryResult> response = restTemplate.postForEntity("/query", entity, PineconeQueryResult.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                logger.error("Pinecone API error: status={}", response.getStatusCode());
                throw new MatchingException("Pinecone API error: " + response.getStatusCode());
            }

            List<PineconeMatch> matches = response.getBody().matches();
            if (matches == null || matches.isEmpty()) {
                logger.warn("No matches found for student: {}", student.name());
                throw new MatchingException("No matches found for student in Pinecone query");
            }
            logger.debug("Retrieved {} matches from Pinecone", matches.size());


            List<StudentVacancyMatch> result = matches.stream()
                    .filter(this::validateMatch)
                    .map(match -> new StudentVacancyMatch(
                            new Vacancy(match.id(),
                                    match.metadata().title(),
                                    match.metadata().link(),
                                    new EmbeddingResult(match.values())),
                            student,
                            match.score()))
                    .toList();

            logger.info("Returning {} matches for student: {}", result.size(), student.name());
            return result;
        } catch (Exception e) {
            logger.error("Pinecone query failed for student: {}, error: {}", student.name(), e.getMessage(), e);
            throw new MatchingException("Pinecone query failed: " + e.getMessage(), e);
        }
    }

    private boolean validateMatch(PineconeMatch match) {
        return (match.metadata() != null) &&
                (match.metadata().title()) != null &&
                (match.metadata().link()) != null;
    }
}
