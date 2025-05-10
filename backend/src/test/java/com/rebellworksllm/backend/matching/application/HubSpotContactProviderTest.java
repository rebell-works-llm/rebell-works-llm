package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.application.dto.StudentDto;
import com.rebellworksllm.backend.matching.config.HubSpotCredentials;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class HubSpotContactProviderTest {

    private HubSpotContactProvider contactProvider;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(List.of((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer test-token");
            return execution.execute(request, body);
        }));

        HubSpotCredentials properties = new HubSpotCredentials();
        properties.setApiKey("test-token");
        properties.setBaseUrl("https://api.hubapi.com");
        properties.setContactProperties("firstname,email,studie,phone,study,op_zoek_naar_,location,geboortedatum");

        contactProvider = new HubSpotContactProvider(restTemplate, properties);
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void testGetStudentById() {
        String jsonResponse = """
                {
                  "id": "1",
                  "properties": {
                    "firstname": "John Doe",
                    "email": "john.doe@example.com",
                    "phone": "0123456789",
                    "studie": "Computer Science",
                    "op_zoek_naar_": "Job opportunities",
                    "location": "Amsterdam",
                    "geboortedatum": "1998-06-15"
                  }
                }""";

        String expectedUrl = "https://api.hubapi.com/crm/v3/objects/contacts/1?properties=firstname,email,studie,phone,study,op_zoek_naar_,location,geboortedatum";

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer test-token"))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        StudentDto studentDto = contactProvider.getByContactId(1L);

        assertEquals("John Doe", studentDto.fullName());
        assertEquals("john.doe@example.com", studentDto.email());
        assertEquals("0123456789", studentDto.phoneNumber());
        assertEquals("Computer Science", studentDto.study());
        assertEquals("Job opportunities", studentDto.text());
        assertEquals("Amsterdam", studentDto.studyLocation());
        assertEquals("1998-06-15", studentDto.expectedGraduationDate());
    }
}
