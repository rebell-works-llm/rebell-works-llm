package com.rebellworksllm.backend.hubspot.application;

import com.rebellworksllm.backend.hubspot.application.dto.StudentRequest;
import com.rebellworksllm.backend.hubspot.application.exception.HubSpotStudentNotFoundException;
import com.rebellworksllm.backend.hubspot.presentation.dto.StudentContact;
import com.rebellworksllm.backend.hubspot.config.HubSpotCredentials;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import java.util.Map;

@Service
public class HubSpotStudentServiceImpl implements HubSpotStudentService {

    private final RestTemplate restTemplate;
    private final HubSpotCredentials credentials;

    public HubSpotStudentServiceImpl(@Qualifier("hubspotRestTemplate") RestTemplate restTemplate, HubSpotCredentials credentials) {
        this.restTemplate = restTemplate;
        this.credentials = credentials;
    }


    @Override
    public StudentContact getStudentById(long id) {
        String url = UriComponentsBuilder.fromUriString(credentials.getApiBaseUrl())
                .path("/crm/v3/objects/contacts/{id}")
                .queryParam("properties", credentials.getContactProperties())
                .buildAndExpand(id)
                .toUriString();

        try {
            ResponseEntity<StudentRequest> response = restTemplate.getForEntity(url, StudentRequest.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new HubSpotStudentNotFoundException("Could not fetch student with ID " + id);
            }
            return mapToStudent(response.getBody());
        } catch (HttpClientErrorException e) {
            throw new HubSpotStudentNotFoundException("HubSpot API error: " + e.getStatusCode());
        }
    }

    private StudentContact mapToStudent(StudentRequest request) {
        if (request.properties() == null) {
            throw new HubSpotStudentNotFoundException("Invalid response: properties are null");
        }
        Map<String, String> props = request.properties();

        String rawPhoneNumber = props.getOrDefault("phone", "");
        String correctlyFormattedNumber = reFormatPhoneNumber(rawPhoneNumber);

        return new StudentContact(
                props.getOrDefault("firstname", ""),
                props.getOrDefault("email", ""),
                correctlyFormattedNumber,
                props.getOrDefault("studie", ""),
                props.getOrDefault("op_zoek_naar_", ""),
                props.getOrDefault("location", ""),
                props.getOrDefault("geboortedatum", "")
        );
    }

    private String reFormatPhoneNumber(String phone) {

        String cleaned = phone.replaceAll("[\\s\\-()]", "");


        if (cleaned.startsWith("+31")) {
            cleaned = cleaned.substring(3);
        } else if (cleaned.startsWith("0")) {
            cleaned = cleaned.substring(1);
        }

        return "31" + cleaned;
    }
}
