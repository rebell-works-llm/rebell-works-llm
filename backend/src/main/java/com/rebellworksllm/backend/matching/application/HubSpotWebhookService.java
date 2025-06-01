package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.hubspot.presentation.dto.StudentContact;
import com.rebellworksllm.backend.openai.presentation.dto.EmbeddingResult;
import com.rebellworksllm.backend.hubspot.application.HubSpotStudentService;
import com.rebellworksllm.backend.matching.domain.*;
import com.rebellworksllm.backend.openai.application.OpenAIEmbeddingService;
import com.rebellworksllm.backend.whatsapp.application.WhatsAppService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HubSpotWebhookService {

    private static final int FIRST_MATCH_LIMIT = 5;

    private final HubSpotStudentService studentService;
    private final VacancyService vacancyService;
    private final OpenAIEmbeddingService embeddingService;
    private final StudentJobMatchingService matchingService;
    private final WhatsAppService whatsAppService;

    public HubSpotWebhookService(HubSpotStudentService studentService,
                                 VacancyService vacancyService,
                                 OpenAIEmbeddingService embeddingService,
                                 StudentJobMatchingService matchingService,
                                 WhatsAppService whatsAppService
    ) {
        this.studentService = studentService;
        this.vacancyService = vacancyService;
        this.embeddingService = embeddingService;
        this.matchingService = matchingService;
        this.whatsAppService = whatsAppService;
    }


    public void startMatchEventForObject(long objectId) {
        StudentContact studentContact = studentService.getStudentById(objectId);

        Student realStudent = toStudent(studentContact);
        List<Vacancy> vacancies = vacancyService.getAllVacancies();
        List<StudentVacancyMatch> matches = matchingService.findBestMatches(realStudent, vacancies, FIRST_MATCH_LIMIT);

        whatsAppService.sendWithVacancyTemplate(
                studentContact.phoneNumber(),
                studentContact.fullName(),
                matches.getFirst().vacancy().website(),
                matches.get(0).vacancy().website(),
                matches.get(1).vacancy().website(),
                matches.get(2).vacancy().website(),
                matches.get(3).vacancy().website()
        );
    }

    private Student toStudent(StudentContact studentContact) {
        EmbeddingResult studentEmbeddingResult = embeddingService.embedText(
                studentContact.study() + " " + studentContact.studyLocation() + " " + studentContact.text()
        );
        return new Student(
                studentContact.fullName(),
                studentContact.email(),
                studentContact.phoneNumber(),
                studentContact.study(),
                studentContact.text(),
                studentContact.studyLocation(),
                studentEmbeddingResult
        );
    }
}
