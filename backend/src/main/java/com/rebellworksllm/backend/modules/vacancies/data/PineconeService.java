package com.rebellworksllm.backend.modules.vacancies.data;

import com.rebellworksllm.backend.modules.matching.application.exception.MatchingException;
import com.rebellworksllm.backend.modules.openai.application.dto.EmbeddingResult;
import com.rebellworksllm.backend.modules.vacancies.application.dto.MatchedVacancy;
import com.rebellworksllm.backend.modules.vacancies.application.dto.VacancyResponseDto;
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

    public List<MatchedVacancy> queryTopMatches(List<Double> vector, int topK) {
        try {
            PineconeQueryRequest request = new PineconeQueryRequest(vector, topK, true, true);
            HttpEntity<PineconeQueryRequest> entity = new HttpEntity<>(request);

            logger.debug("Requesting {} pinecone matches (vacancies)", topK);
            ResponseEntity<PineconeQueryResult> response = restTemplate.postForEntity("/query", entity, PineconeQueryResult.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                logger.error("Pinecone API error: status={}", response.getStatusCode());
                throw new MatchingException("Pinecone API error: " + response.getStatusCode());
            }

            List<PineconeMatchResponse> matches = response.getBody().matches();
            if (matches == null || matches.isEmpty()) {
                logger.warn("No matches found");
                throw new MatchingException("No matches found");
            }

            List<MatchedVacancy> result = matches.stream()
                    .filter(this::validateMatch)
                    .map(match -> new MatchedVacancy(
                            new VacancyResponseDto(match.id(),
                                    match.metadata().title(),
                                    match.metadata().description(),
                                    match.metadata().working_hours(),
                                    match.metadata().salary(),
                                    match.metadata().link(),
                                    match.metadata().position(),
                                    0,
                                    0),
                            new EmbeddingResult(match.values()),
                            match.score()))
                    .toList();

            List<String> vacancyIds = result.stream()
                    .map(v -> v.vacancyResponse().id())
                    .filter(id -> !id.isEmpty())
                    .toList();

            logger.info("Found {} pinecone matches, IDs: {}", matches.size(), vacancyIds);
            return result;
        } catch (Exception e) {
            logger.error("Pinecone query failed for student: {}", e.getMessage(), e);
            throw new MatchingException("Pinecone query failed: " + e.getMessage(), e);
        }
    }

    private boolean validateMatch(PineconeMatchResponse match) {
        if (match == null || match.metadata() == null || match.values() == null) {
            return false;
        }

        var metadata = match.metadata();
        return metadata.title() != null &&
                metadata.description() != null &&
                metadata.working_hours() != null &&
                metadata.salary() != null &&
                metadata.position() != null;
    }
}
