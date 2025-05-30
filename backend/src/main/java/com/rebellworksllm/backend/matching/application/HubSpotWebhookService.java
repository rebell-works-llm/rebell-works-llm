package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.hubspot.domain.StudentContact;
import com.rebellworksllm.backend.openai.domain.EmbeddingResult;
import com.rebellworksllm.backend.hubspot.application.HubSpotStudentService;
import com.rebellworksllm.backend.matching.domain.*;
import com.rebellworksllm.backend.openai.domain.OpenAIEmbeddingService;
import com.rebellworksllm.backend.whatsapp.domain.WhatsAppService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HubSpotWebhookService {

    private static final int FIRST_MATCH_LIMIT = 5;

    private final MatchEngine matchEngine;
    private final HubSpotStudentService studentService;
    private final OpenAIEmbeddingService embeddingService;
    private final WhatsAppService whatsAppService;

    public HubSpotWebhookService(MatchEngine matchEngine,
                                 HubSpotStudentService studentService,
                                 OpenAIEmbeddingService embeddingService,
                                 WhatsAppService whatsAppService
    ) {
        this.matchEngine = matchEngine;
        this.studentService = studentService;
        this.embeddingService = embeddingService;
        this.whatsAppService = whatsAppService;
    }

    public void processStudentMatch(long id) {
        StudentContact studentContact = studentService.getStudentById(id);
        Student student = toStudent(studentContact);

        List<StudentVacancyMatch> matches = matchEngine.query(student, FIRST_MATCH_LIMIT);

        try {
            whatsAppService.sendWithVacancyTemplate(
                    studentContact.phoneNumber(),
                    studentContact.fullName(),
                    matches.getFirst().vacancy().website(),
                    matches.get(0).vacancy().website(),
                    matches.get(1).vacancy().website(),
                    matches.get(2).vacancy().website(),
                    matches.get(3).vacancy().website()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Student toStudent(StudentContact studentContact) {
        EmbeddingResult studentEmbeddingResult = embeddingService.embedText(studentContact.stringify());
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
