package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.application.dto.PineconeMatch;
import com.rebellworksllm.backend.matching.application.dto.PineconeQueryRequest;
import com.rebellworksllm.backend.matching.application.dto.PineconeQueryResult;
import com.rebellworksllm.backend.matching.application.exception.MatchingException;
import com.rebellworksllm.backend.matching.domain.Student;
import com.rebellworksllm.backend.matching.domain.StudentVacancyMatch;
import com.rebellworksllm.backend.matching.domain.Vacancy;
import com.rebellworksllm.backend.openai.domain.EmbeddingResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class PineconeService {

    private final RestTemplate restTemplate;

    public PineconeService(@Qualifier("pineconeRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<StudentVacancyMatch> queryTopMatches(Student student, int topK) {
        try {
            PineconeQueryRequest request = new PineconeQueryRequest(
                    student.embeddingResult().embeddings(),
                    topK,
                    true,
                    true
            );

            HttpEntity<PineconeQueryRequest> entity = new HttpEntity<>(request);
            ResponseEntity<PineconeQueryResult> response = restTemplate.postForEntity("/query", entity, PineconeQueryResult.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new MatchingException("Pinecone API error: " + response.getStatusCode());
            }

            List<PineconeMatch> matches = response.getBody().matches();

            return matches.stream()
                    .filter(this::validateMatch)
                    .map(match -> new StudentVacancyMatch(
                            new Vacancy(match.metadata().title(), match.metadata().link(), new EmbeddingResult(match.values())),
                            student,
                            match.score()))
                    .toList();
        } catch (Exception e) {
            throw new MatchingException("Pinecone query failed: " + e.getMessage(), e);
        }
    }

    private boolean validateMatch(PineconeMatch match) {
        return (match.metadata() != null) &&
                (match.metadata().title()) != null &&
                (match.metadata().link()) != null;
    }
}
