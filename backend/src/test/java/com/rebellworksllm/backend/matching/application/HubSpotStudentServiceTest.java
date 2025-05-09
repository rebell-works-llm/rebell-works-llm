package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.application.dto.StudentDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class HubSpotStudentServiceTest {

    private HubSpotStudentService studentService;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer test-token");
            return execution.execute(request, body);
        });

        studentService = new HubSpotStudentService(restTemplate);
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

        mockServer.expect(requestTo("https://api.hubapi.com/crm/v3/objects/contacts/1?properties=firstname,email,studie,phone,study,op_zoek_naar_,location,geboortedatum"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer test-token"))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        StudentDto studentDto = studentService.getStudentById(1L);

        // Assertions
        assertEquals("John Doe", studentDto.fullName());
        assertEquals("john.doe@example.com", studentDto.email());
        assertEquals("0123456789", studentDto.phoneNumber());
        assertEquals("Computer Science", studentDto.study());
        assertEquals("Job opportunities", studentDto.text());
        assertEquals("Amsterdam", studentDto.studyLocation());
        assertEquals("1998-06-15", studentDto.expectedGraduationDate());
    }
}
