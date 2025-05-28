package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.hubspot.application.HubSpotStudentService;
import com.rebellworksllm.backend.hubspot.presentation.dto.StudentContact;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class HubSpotStudentContactServiceImplIntegrationTest {

    @Autowired
    private HubSpotStudentService studentService;

    @Test
    void testGetStudentById() {
        StudentContact studentContact = studentService.getStudentById(287811507444L);

        assertEquals("Jayden Roeper", studentContact.fullName());
        assertEquals("HBO ICT Software Development", studentContact.study());
        assertEquals("I am looking for a job/ internship where I can improve my frontend and backend development skills building web apps.", studentContact.text());
        assertEquals("Utrecht", studentContact.studyLocation());
        assertEquals("2027-09-01", studentContact.expectedGraduationDate());
    }
}
