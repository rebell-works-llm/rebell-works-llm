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
        logger.debug("Creating new match message: {}", request);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Prefer", "return=representation");

            HttpEntity<List<MatchMessageRequest>> entity = new HttpEntity<>(List.of(request), headers);

            ResponseEntity<MatchMessageResponse[]> response = restTemplate.postForEntity(
                    "/rest/v1/messages",
                    entity,
                    MatchMessageResponse[].class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                logger.error("Supabase API error for INSERT: status={}", response.getStatusCode());
                throw new MatchingException("Supabase INSERT error: " + response.getStatusCode());
            }

            return Arrays.stream(response.getBody())
                    .findFirst()
                    .orElseThrow(() -> new MatchingException("Insert did not return data."));

        } catch (Exception e) {
            logger.error("Failed to insert match message: {}", e.getMessage(), e);
            throw new MatchingException("Supabase insert failed: " + e.getMessage(), e);
        }
    }

    public MatchMessageResponse findByContactPhone(String contactPhone) {
        logger.debug("Fetching match messages for phone: {}", contactPhone);
        try {
            String path = UriComponentsBuilder
                    .fromPath("/rest/v1/messages")
                    .queryParam("contactPhone", "eq." + contactPhone)
                    .toUriString();

            ResponseEntity<MatchMessageResponse[]> response =
                    restTemplate.getForEntity(path, MatchMessageResponse[].class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                logger.error("Supabase API error for GET: status={}", response.getStatusCode());
                throw new MatchingException("Supabase GET error: " + response.getStatusCode());
            }

            return Arrays.stream(response.getBody()).findFirst().orElse(null);

        } catch (Exception e) {
            logger.error("Failed to fetch match messages: {}", e.getMessage(), e);
            throw new MatchingException("Supabase fetch failed: " + e.getMessage(), e);
        }
    }
}
