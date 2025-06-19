package com.rebellworksllm.backend.vacancies.data;

import com.rebellworksllm.backend.matching.application.exception.MatchingException;
import com.rebellworksllm.backend.vacancies.application.dto.PineconeMatchResponse;
import com.rebellworksllm.backend.vacancies.application.dto.PineconeQueryRequest;
import com.rebellworksllm.backend.vacancies.application.dto.PineconeQueryResult;
import com.rebellworksllm.backend.vacancies.domain.ScoredVacancy;
import com.rebellworksllm.backend.vacancies.domain.Vacancy;
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

    public List<ScoredVacancy> queryTopMatches(List<Double> vector, int topK) {
        try {
            PineconeQueryRequest request = new PineconeQueryRequest(vector, topK, true, true);
            HttpEntity<PineconeQueryRequest> entity = new HttpEntity<>(request);

            logger.debug("Sending Pinecone query with topK: {}", topK);
            ResponseEntity<PineconeQueryResult> response = restTemplate.postForEntity("/query", entity, PineconeQueryResult.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                logger.error("Pinecone API error: status={}", response.getStatusCode());
                throw new MatchingException("Pinecone API error: " + response.getStatusCode());
            }

            List<PineconeMatchResponse> matches = response.getBody().matches();
            if (matches == null || matches.isEmpty()) {
                logger.warn("No matches found");
                throw new MatchingException("No matches found for student in Pinecone query");
            }
            logger.debug("Retrieved {} matches from Pinecone", matches.size());


            List<ScoredVacancy> result = matches.stream()
                    .filter(this::validateMatch)
                    .map(match -> new ScoredVacancy(
                            new Vacancy(match.id(),
                                    match.metadata().title(),
                                    match.metadata().link()
                            ),
                            match.score()))
                    .toList();

            logger.info("Returning {} matches", result.size());
            return result;
        } catch (Exception e) {
            logger.error("Pinecone query failed for student: {}", e.getMessage(), e);
            throw new MatchingException("Pinecone query failed: " + e.getMessage(), e);
        }
    }

    private boolean validateMatch(PineconeMatchResponse match) {
        return (match.metadata() != null) &&
                (match.metadata().title()) != null &&
                (match.metadata().link()) != null;
    }
}
