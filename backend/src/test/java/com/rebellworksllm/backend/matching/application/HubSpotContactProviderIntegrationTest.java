package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.matching.application.dto.StudentDto;
import com.rebellworksllm.backend.matching.config.HubSpotCredentials;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@EnableConfigurationProperties(HubSpotCredentials.class)
public class HubSpotContactProviderIntegrationTest {

    @Autowired
    private HubSpotContactProvider contactProvider;

    @Autowired
    private HubSpotCredentials hubSpotCredentials;


    @Test
    void testGetStudentById() {
        StudentDto studentDto = contactProvider.getByIdWithProperties(287811507444L, hubSpotCredentials.getContactProperties());

        assertEquals("Jayden", studentDto.fullName());
        assertEquals("HBO ICT Software Development", studentDto.study());
        assertEquals("I am looking for a job/ internship where I can improve my frontend and backend development skills building web apps.", studentDto.text());
        assertEquals("Utrecht", studentDto.studyLocation());
        assertEquals("2027-07-01", studentDto.expectedGraduationDate());
    }
}
