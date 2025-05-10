package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.embedding.domain.TextEmbedder;
import com.rebellworksllm.backend.embedding.domain.Vectors;
import com.rebellworksllm.backend.matching.application.dto.StudentDto;
import com.rebellworksllm.backend.matching.domain.*;
import com.rebellworksllm.backend.whatsapp.application.WhatsAppService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HubSpotWebhookService {

    private static final int FIRST_MATCH_LIMIT = 5;

    private final ContactProvider studentService;
    private final VacancyService vacancyService;
    private final TextEmbedder embedder;
    private final StudentJobMatchingService matchingService;
    private final WhatsAppService whatsAppService;

    public HubSpotWebhookService(ContactProvider studentService,
                                 VacancyService vacancyService,
                                 TextEmbedder embedder,
                                 StudentJobMatchingService matchingService,
                                 WhatsAppService whatsAppService) {
        this.studentService = studentService;
        this.vacancyService = vacancyService;
        this.embedder = embedder;
        this.matchingService = matchingService;
        this.whatsAppService = whatsAppService;
    }

    public void startMatchEventForObject(long objectId) {
        StudentDto studentDto = studentService.getByContactId(objectId);
        Student student = toStudent(studentDto);
        List<Vacancy> vacancies = vacancyService.getAllVacancies();
        List<StudentVacancyMatch> matches = matchingService.findBestMatches(student, vacancies, FIRST_MATCH_LIMIT);

        whatsAppService.sendWithVacancyTemplate(
                student.phoneNumber(),
                student.name(),
                matches.getFirst().vacancy().website(),
                matches.get(0).vacancy().website(),
                matches.get(1).vacancy().website(),
                matches.get(2).vacancy().website(),
                matches.get(3).vacancy().website()
        );
    }

    private Student toStudent(StudentDto studentDto) {
        Vectors studentVectors = embedder.embedText(
                studentDto.study() + " " + studentDto.studyLocation() + " " + studentDto.text()
        );
        return new Student(
                studentDto.fullName(),
                studentDto.email(),
                studentDto.phoneNumber(),
                studentDto.study(),
                studentDto.text(),
                studentDto.studyLocation(),
                studentVectors
        );
    }
}
