package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.application.dto.ContactRequest;
import com.rebellworksllm.backend.matching.application.dto.StudentDto;
import com.rebellworksllm.backend.matching.application.exception.StudentNotFoundException;
import com.rebellworksllm.backend.matching.domain.ContactProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static com.rebellworksllm.backend.matching.application.mapper.ContactMapper.toStudentDto;

@Service
public class HubSpotContactProvider implements ContactProvider {

    private static final String HUBSPOT_CRM_CONTACT_URL = "https://api.hubapi.com/crm/v3/objects/contacts";

    private final RestTemplate restTemplate;

    public HubSpotContactProvider(@Qualifier("HubSpotRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public StudentDto getStudentById(long id) {
        String url = UriComponentsBuilder.fromUriString(HUBSPOT_CRM_CONTACT_URL)
                .path("/{id}")
                .queryParam("properties", "firstname,email,studie,phone,study,op_zoek_naar_,location,geboortedatum")
                .buildAndExpand(id)
                .toUriString();

        ResponseEntity<ContactRequest> response = restTemplate.getForEntity(url, ContactRequest.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new StudentNotFoundException("Could not fetch contact with ID " + id);
        }

        return toStudentDto(response.getBody());
    }
}
