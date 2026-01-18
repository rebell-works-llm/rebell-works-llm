package com.rebellworksllm.backend.modules.matching.data;

import com.rebellworksllm.backend.common.utils.LogUtils;
import com.rebellworksllm.backend.modules.matching.application.exception.MatchingException;
import com.rebellworksllm.backend.modules.matching.application.util.MatchingUtils;
import com.rebellworksllm.backend.modules.matching.data.dto.MatchMessageRequest;
import com.rebellworksllm.backend.modules.matching.data.dto.MatchMessageResponse;
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
        if (request == null) {
            throw new MatchingException("MatchMessageRequest must not be null");
        }

        String normalizedPhone = MatchingUtils.normalizePhone(request.contactPhone());
        if (normalizedPhone == null || normalizedPhone.isBlank()) {
            throw new MatchingException("contactPhone is missing/invalid; cannot persist message");
        }

        MatchMessageRequest normalizedRequest = new MatchMessageRequest(
                request.vacancyIds(),
                normalizedPhone
        );

        logger.debug("Saving match message (normalized phone): {}", normalizedRequest);

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
        String normalizedPhone = MatchingUtils.normalizePhone(contactPhone);
        if (normalizedPhone == null || normalizedPhone.isBlank()) {
            throw new MatchingException("contactPhone is missing/invalid; cannot fetch message");
        }

        logger.debug("Fetching match message for phone (normalized): {}", LogUtils.maskPhone(normalizedPhone));

        String path = UriComponentsBuilder
                .fromPath("/rest/v1/messages")
                .queryParam("contactPhone", "eq." + normalizedPhone)
                .toUriString();

        try {
            ResponseEntity<MatchMessageResponse[]> response = restTemplate.getForEntity(
                    path, MatchMessageResponse[].class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                logger.error("Supabase GET error: status={}, body={}", response.getStatusCode(), response.getBody());
                throw new MatchingException("Supabase GET error: " + response.getStatusCode());
            }

            return Arrays.stream(response.getBody())
                    .findFirst()
                    .orElseThrow(() -> new MatchingException("No match message found for phone: " + normalizedPhone));

        } catch (Exception e) {
            logger.error("Failed to fetch match message: {}", e.getMessage(), e);
            throw new MatchingException("Supabase fetch failed: " + e.getMessage(), e);
        }
    }
}
