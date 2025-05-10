package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.application.dto.ContactRequest;
import com.rebellworksllm.backend.matching.application.dto.StudentDto;
import com.rebellworksllm.backend.matching.application.exception.ContactNotFoundException;
import com.rebellworksllm.backend.matching.config.HubSpotProperties;
import com.rebellworksllm.backend.matching.domain.ContactProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static com.rebellworksllm.backend.matching.application.mapper.ContactMapper.toStudentDto;

@Service
public class HubSpotContactProvider implements ContactProvider {

    @Qualifier("hubspotRestTemplate")
    private final RestTemplate restTemplate;
    private final HubSpotProperties properties;

    public HubSpotContactProvider(RestTemplate restTemplate, HubSpotProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public StudentDto getByContactId(long id) {
        String url = UriComponentsBuilder.fromUriString(properties.getBaseUrl())
                .path("/crm/v3/objects/contacts/{id}")
                .queryParam("properties", properties.getContactProperties())
                .buildAndExpand(id)
                .toUriString();

        ResponseEntity<ContactRequest> response = restTemplate.getForEntity(url, ContactRequest.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new ContactNotFoundException("Could not fetch contact with ID " + id);
        }

        return toStudentDto(response.getBody());
    }
}
