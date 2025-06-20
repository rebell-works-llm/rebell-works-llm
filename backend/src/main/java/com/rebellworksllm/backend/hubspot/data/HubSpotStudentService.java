package com.rebellworksllm.backend.hubspot.data;

import com.rebellworksllm.backend.hubspot.application.exception.HubSpotStudentNotFoundException;
import com.rebellworksllm.backend.hubspot.application.dto.StudentContact;
import com.rebellworksllm.backend.hubspot.config.HubSpotCredentials;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import java.util.List;
import java.util.Map;

@Service
public class HubSpotStudentService {

    private final RestTemplate restTemplate;
    private final HubSpotCredentials credentials;

    public HubSpotStudentService(@Qualifier("hubspotRestTemplate") RestTemplate restTemplate, HubSpotCredentials credentials) {
        this.restTemplate = restTemplate;
        this.credentials = credentials;
    }


    public StudentContact getStudentById(long id) {
        String url = UriComponentsBuilder.fromUriString(credentials.getApiBaseUrl())
                .path("/crm/v3/objects/contacts/{id}")
                .queryParam("properties", credentials.getContactProperties())
                .buildAndExpand(id)
                .toUriString();

        try {
            ResponseEntity<StudentRequest> response = restTemplate.getForEntity(url, StudentRequest.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new HubSpotStudentNotFoundException("Student not found with ID: " + id);
            }
            return mapToStudent(response.getBody());
        } catch (HttpClientErrorException e) {
            throw new HubSpotStudentNotFoundException("Could not fetch student with ID: " + id);
        }
    }

    public StudentContact getStudentByPhone(String phone) {
        String url = UriComponentsBuilder.fromUriString(credentials.getApiBaseUrl())
                .path("/crm/v3/objects/contacts/search")
                .toUriString();

        Map<String, Object> searchRequest = Map.of(
                "filterGroups", List.of(Map.of(
                        "filters", List.of(Map.of(
                                "propertyName", "phone",
                                "operator", "CONTAINS_TOKEN",
                                "value", phone
                        ))
                )),
                "properties", List.of(
                        "email", "firstname", "geboortedatum",
                        "phone", "studie", "location", "op_zoek_naar_"
                ),
                "limit", 1,
                "after", 0
        );

        try {
            ResponseEntity<StudentSearchResponse> response = restTemplate.postForEntity(
                    url,
                    searchRequest,
                    StudentSearchResponse.class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null
                    || response.getBody().results() == null || response.getBody().results().isEmpty()) {
                throw new HubSpotStudentNotFoundException("Student not found with phone: " + phone);
            }

            StudentRequest student = response.getBody().results().getFirst();
            return mapToStudent(student);

        } catch (HttpClientErrorException e) {
            throw new HubSpotStudentNotFoundException("Could not fetch student with phone: " + phone);
        }
    }

    private StudentContact mapToStudent(StudentRequest request) {
        if (request.properties() == null) {
            throw new HubSpotStudentNotFoundException("Invalid response: properties are null");
        }
        Map<String, String> props = request.properties();
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
