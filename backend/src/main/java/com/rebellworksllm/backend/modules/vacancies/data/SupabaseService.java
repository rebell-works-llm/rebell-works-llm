package com.rebellworksllm.backend.modules.vacancies.data;

import com.rebellworksllm.backend.modules.matching.application.exception.MatchingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;

@Service
public class SupabaseService {

    private static final Logger logger = LoggerFactory.getLogger(SupabaseService.class);

    private final RestTemplate restTemplate;

    public SupabaseService(@Qualifier("supabaseRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public SupabaseResponse getVacancyById(String id) {
        logger.debug("Fetching vacancy from Supabase with id: {}", id);
        try {
            ResponseEntity<SupabaseResponse[]> response =
                    restTemplate.getForEntity("/rest/v1/vacancies?id=eq." + id, SupabaseResponse[].class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                logger.error("Supabase API error for GET: status={}", response.getStatusCode());
                throw new MatchingException("Supabase GET error: " + response.getStatusCode());
            }

            System.out.println(Arrays.toString(response.getBody()));
            return Arrays.stream(response.getBody())
                    .findFirst()
                    .orElseThrow(() -> new MatchingException("Vacancy not found for id: " + id));

        } catch (Exception e) {
            logger.error("Failed to retrieve vacancy: {}", e.getMessage(), e);
            throw new MatchingException("Supabase fetch failed: " + e.getMessage(), e);
        }
    }

    public void updateMatchCount(String id) {
        logger.debug("Incrementing matchCount for vacancy id: {}", id);
        try {
            SupabaseResponse supabaseResponse = getVacancyById(id);
            int newCount = supabaseResponse.matchCount() + 1;
            SupabaseUpdateRequest requestBody = new SupabaseUpdateRequest(newCount);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Prefer", "return=minimal");

            HttpEntity<SupabaseUpdateRequest> entity = new HttpEntity<>(requestBody, headers);

            URI uri = UriComponentsBuilder
                    .fromPath("/rest/v1/vacancies")
                    .queryParam("id", "eq." + id)
                    .build()
                    .toUri();


            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.PATCH, entity, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("Supabase API error for PATCH: status={}", response.getStatusCode());
                throw new MatchingException("Supabase PATCH error: " + response.getStatusCode());
            }

            logger.info("Successfully updated matchCount for id: {}", id);

        } catch (Exception e) {
            logger.error("Failed to update matchCount for id {}: {}", id, e.getMessage(), e);
            throw new MatchingException("Supabase update failed: " + e.getMessage(), e);
        }
    }
}
