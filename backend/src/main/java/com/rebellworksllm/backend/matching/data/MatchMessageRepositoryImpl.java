package com.rebellworksllm.backend.matching.data;

import com.rebellworksllm.backend.matching.application.exception.MatchingException;
import com.rebellworksllm.backend.matching.data.dto.MatchMessageRequest;
import com.rebellworksllm.backend.matching.data.dto.MatchMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

@Service
public class MatchMessageRepositoryImpl implements MatchMessageRepository {

    private static final Logger logger = LoggerFactory.getLogger(MatchMessageRepositoryImpl.class);

    private final RestTemplate restTemplate;

    public MatchMessageRepositoryImpl(@Qualifier("supabaseRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public MatchMessageResponse save(MatchMessageRequest request) {
        logger.debug("Saving match message: {}", request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Prefer", "return=representation");
        HttpEntity<List<MatchMessageRequest>> entity = new HttpEntity<>(List.of(request), headers);

        try {
            ResponseEntity<MatchMessageResponse[]> response = restTemplate.postForEntity(
                    "/rest/v1/messages", entity, MatchMessageResponse[].class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                logger.error("Supabase INSERT error: status={}, body={}", response.getStatusCode(), response.getBody());
                throw new MatchingException("Supabase INSERT error: " + response.getStatusCode());
            }

            return Arrays.stream(response.getBody())
                    .findFirst()
                    .orElseThrow(() -> new MatchingException("Insert did not return data."));

        } catch (Exception e) {
            logger.error("Failed to save match message: {}", e.getMessage(), e);
            throw new MatchingException("Supabase insert failed: " + e.getMessage(), e);
        }
    }

    public MatchMessageResponse findByContactPhone(String contactPhone) {
        logger.debug("Fetching match message for phone: {}", contactPhone);

        String path = UriComponentsBuilder
                .fromPath("/rest/v1/messages")
                .queryParam("contactPhone", "eq." + contactPhone)
                .toUriString();

        try {
            ResponseEntity<MatchMessageResponse[]> response = restTemplate.getForEntity(
                    path, MatchMessageResponse[].class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                logger.error("Supabase GET error: status={}, body={}", response.getStatusCode(), response.getBody());
                throw new MatchingException("Supabase GET error: " + response.getStatusCode());
            }

            return Arrays.stream(response.getBody()).findFirst().orElse(null);

        } catch (Exception e) {
            logger.error("Failed to fetch match message: {}", e.getMessage(), e);
            throw new MatchingException("Supabase fetch failed: " + e.getMessage(), e);
        }
    }
}
