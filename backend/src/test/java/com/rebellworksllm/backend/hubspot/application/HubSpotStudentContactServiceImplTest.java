package com.rebellworksllm.backend.hubspot.application;

import com.rebellworksllm.backend.hubspot.application.dto.StudentContact;
import com.rebellworksllm.backend.hubspot.config.HubSpotCredentials;
import com.rebellworksllm.backend.hubspot.data.HubSpotStudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class HubSpotStudentContactServiceImplTest {

    private HubSpotStudentProvider studentProvider;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(List.of((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer test-token");
            return execution.execute(request, body);
        }));

        HubSpotCredentials credentials = new HubSpotCredentials();
        credentials.setApiKey("test-token");
        credentials.setApiBaseUrl("https://api.hubapi.com");
        credentials.setContactProperties("firstname,email,studie,phone,study,op_zoek_naar_,location,geboortedatum");

        HubSpotStudentService studentService = new HubSpotStudentService(restTemplate, credentials);
        studentProvider = new HubSpotStudentProviderService(studentService);
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

        StudentContact studentContact = studentProvider.getStudentById(1L);

        assertEquals("John Doe", studentContact.fullName());
        assertEquals("john.doe@example.com", studentContact.email());
        assertEquals("0123456789", studentContact.phoneNumber());
        assertEquals("Computer Science", studentContact.study());
        assertEquals("Job opportunities", studentContact.text());
        assertEquals("Amsterdam", studentContact.studyLocation());
        assertEquals("1998-06-15", studentContact.expectedGraduationDate());
    }
}
