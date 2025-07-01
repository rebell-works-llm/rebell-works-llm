package com.rebellworksllm.backend.hubspot.application;

import com.rebellworksllm.backend.modules.hubspot.application.HubSpotStudentProvider;
import com.rebellworksllm.backend.modules.hubspot.application.dto.StudentContact;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("prod")
public class HubSpotStudentContactServiceImplIntegrationTest {

    @Autowired
    private HubSpotStudentProvider studentProvider;

    @Value("${hubspot.test-phone}")
    private String phone;

    @Test
    void testGetStudentById() {
        StudentContact studentContact = studentProvider.getStudentById(287811507444L);

        assertEquals("Jayden Roeper", studentContact.fullName());
        assertEquals("HBO ICT Software Development", studentContact.study());
        assertEquals("I am looking for a job/ internship where I can improve my frontend and backend development skills building web apps.", studentContact.text());
        assertEquals("Utrecht", studentContact.studyLocation());
        assertEquals("2027-09-01", studentContact.expectedGraduationDate());
        assertTrue(studentContact.phoneNumber().startsWith("+316"));
    }

    @Test
    void testGetStudentByPhone() {
        StudentContact studentContact = studentProvider.getStudentByPhone(phone);

        assertEquals("Jayden Roeper", studentContact.fullName());
        assertEquals("HBO ICT Software Development", studentContact.study());
        assertEquals("I am looking for a job/ internship where I can improve my frontend and backend development skills building web apps.", studentContact.text());
        assertEquals("Utrecht", studentContact.studyLocation());
        assertEquals("2027-09-01", studentContact.expectedGraduationDate());
        assertTrue(studentContact.phoneNumber().startsWith("+316"));
    }
}
