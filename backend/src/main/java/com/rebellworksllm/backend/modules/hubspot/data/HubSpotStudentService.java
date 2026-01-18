package com.rebellworksllm.backend.modules.hubspot.data;

import com.rebellworksllm.backend.common.utils.LogUtils;
import com.rebellworksllm.backend.modules.hubspot.application.exception.HubSpotStudentNotFoundException;
import com.rebellworksllm.backend.modules.hubspot.application.dto.StudentContact;
import com.rebellworksllm.backend.modules.hubspot.config.HubSpotCredentials;
import com.rebellworksllm.backend.modules.matching.application.util.MatchingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.rebellworksllm.backend.common.utils.LogUtils.maskPhone;

@Service
public class HubSpotStudentService {

    private static final Logger logger = LoggerFactory.getLogger(HubSpotStudentService.class);

    private final RestTemplate restTemplate;
    private final HubSpotCredentials credentials;

    public HubSpotStudentService(@Qualifier("hubspotRestTemplate") RestTemplate restTemplate, HubSpotCredentials credentials) {
        this.restTemplate = restTemplate;
        this.credentials = credentials;
    }

    /**
     * Retrieve a student contact from HubSpot by their ID.
     */
    public StudentContact getStudentById(final long id) {
        final String url = UriComponentsBuilder.fromUriString(credentials.getApiBaseUrl())
                .path("/crm/v3/objects/contacts/{id}")
                .queryParam("properties", credentials.getContactProperties())
                .buildAndExpand(id)
                .toUriString();

        logger.info("Fetching student from HubSpot by ID: {}", id);

        try {
            final ResponseEntity<StudentRequest> response = restTemplate.getForEntity(url, StudentRequest.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                logger.warn("Student with ID {} not found or invalid response. Status: {}, Body: {}",
                        id, response.getStatusCode(), response.getBody());
                throw new HubSpotStudentNotFoundException("Student not found with ID: " + id);
            }

            logger.info("Successfully fetched student from HubSpot with ID: {}", id);
            return mapToStudent(response.getBody());
        } catch (HttpClientErrorException e) {
            logger.error("HubSpot HTTP error on getStudentById (ID {}): status={}, response={}",
                    id, e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new HubSpotStudentNotFoundException("Could not fetch student with ID: " + id, e);
        } catch (Exception e) {
            logger.error("Unexpected error in getStudentById (ID {}): {}", id, e.getMessage(), e);
            throw new HubSpotStudentNotFoundException("Unexpected error fetching student with ID: " + id, e);
        }
    }

    public StudentContact getStudentByPhone(final String phone) {
        Objects.requireNonNull(phone, "Phone must not be null");

        final String url = UriComponentsBuilder.fromUriString(credentials.getApiBaseUrl())
                .path("/crm/v3/objects/contacts/search")
                .toUriString();

        List<String> phoneVariants = MatchingUtils.createDutchPhoneVariantsForHubSpot(phone);

        Map<String, Object> searchRequest = Map.of(
                "filterGroups", List.of(Map.of(
                        "filters", List.of(Map.of(
                                "propertyName", "phone",
                                "operator", "IN",
                                "values", phoneVariants
                        ))
                )),
                "properties", List.of(
                        "email", "firstname", "geboortedatum",
                        "phone", "studie", "location", "op_zoek_naar_"
                ),
                "limit", 1,
                "after", 0
        );

        logger.info(
                "Searching for student in HubSpot by phone variants: {}",
                phoneVariants.stream().map(LogUtils::maskPhone).toList()
        );

        try {
            final ResponseEntity<StudentSearchResponse> response = restTemplate.postForEntity(
                    url, searchRequest, StudentSearchResponse.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null
                    || response.getBody().results() == null || response.getBody().results().isEmpty()) {
                logger.warn("Student with phone {} not found or invalid response. Status: {}, Body: {}",
                        maskPhone(phone), response.getStatusCode(), response.getBody());
                throw new HubSpotStudentNotFoundException("Student not found with phone: " + phone);
            }

            final StudentRequest student = response.getBody().results().getFirst();
            logger.info("Successfully fetched student from HubSpot with phone: {}", maskPhone(phone));

            return mapToStudent(student);
        } catch (HttpClientErrorException e) {
            logger.error("HubSpot HTTP error on getStudentByPhone (phone {}): status={}, response={}",
                    maskPhone(phone), e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new HubSpotStudentNotFoundException("Could not fetch student with phone: " + maskPhone(phone), e);
        } catch (Exception e) {
            logger.error("Unexpected error in getStudentByPhone (phone {}): {}", maskPhone(phone), e.getMessage(), e);
            throw new HubSpotStudentNotFoundException("Unexpected error fetching student with phone: " + maskPhone(phone), e);
        }
    }

    private StudentContact mapToStudent(final StudentRequest request) {
        if (request == null || request.properties() == null) {
            logger.error("Student response contains null properties (id: {})", Optional.ofNullable(request).map(StudentRequest::id).orElse(null));
            throw new HubSpotStudentNotFoundException("Invalid response: properties are null");
        }

        final Map<String, String> props = request.properties();
        return new StudentContact(
                request.id(),
                props.getOrDefault("firstname", ""),
                props.getOrDefault("email", ""),
                props.getOrDefault("phone", ""),
                props.getOrDefault("studie", ""),
                props.getOrDefault("op_zoek_naar_", ""),
                props.getOrDefault("location", ""),
                props.getOrDefault("geboortedatum", "")
        );
    }
}
