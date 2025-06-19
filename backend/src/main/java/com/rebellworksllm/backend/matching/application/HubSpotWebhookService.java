package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.email.application.EmailService;
import com.rebellworksllm.backend.hubspot.domain.StudentContact;
import com.rebellworksllm.backend.matching.application.exception.InsufficientMatchesException;
import com.rebellworksllm.backend.openai.domain.EmbeddingResult;
import com.rebellworksllm.backend.hubspot.application.HubSpotStudentService;
import com.rebellworksllm.backend.matching.domain.*;
import com.rebellworksllm.backend.openai.domain.OpenAIEmbeddingService;
import com.rebellworksllm.backend.whatsapp.domain.WhatsAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.rebellworksllm.backend.matching.application.util.LogUtils.maskPhone;

@Service
public class HubSpotWebhookService {

    private static final Logger logger = LoggerFactory.getLogger(HubSpotWebhookService.class);
    private static final int FIRST_MATCH_LIMIT = 5;

    private final StudentJobMatchEngine matchEngine;
    private final HubSpotStudentService studentService;
    private final OpenAIEmbeddingService embeddingService;
    private final EmailService emailService;
    private final WhatsAppService whatsAppService;
    private final VacancyNotificationAdapter vacancyNotificationAdapter;

    public HubSpotWebhookService(StudentJobMatchEngine matchEngine,
                                 HubSpotStudentService studentService,
                                 OpenAIEmbeddingService embeddingService,
                                 WhatsAppService whatsAppService,
                                 EmailService emailService,
                                 VacancyNotificationAdapter vacancyNotificationAdapter) {
        this.matchEngine = matchEngine;
        this.studentService = studentService;
        this.embeddingService = embeddingService;
        this.whatsAppService = whatsAppService;
        this.vacancyNotificationAdapter = vacancyNotificationAdapter;
        this.emailService = emailService;
    }

    @Async("jobMatchingExecutor")
    public void processStudentMatch(long id) {
        logger.info("Starting matching progress for ID: {}", id);
        StudentContact studentContact = studentService.getStudentById(id);
        logger.debug("Fetched HubSpot student: {}", studentContact.fullName());
        Student student = toStudent(studentContact);
        List<StudentVacancyMatch> matches = matchEngine.query(student, FIRST_MATCH_LIMIT);

        if (matches.size() < FIRST_MATCH_LIMIT) {
            logger.warn("Insufficient matches found for student: {}, required: {}, found: {}", studentContact.fullName(), FIRST_MATCH_LIMIT, matches.size());
            throw new InsufficientMatchesException("Insufficient vacancy matches found for student: " + studentContact.fullName());
        }

        List<String> vacancyIds = getVacancyIds(matches);
        logger.info("Retrieved {} matches for student: {} with vacancy IDs: {}", matches.size(), studentContact.fullName(), vacancyIds);

        try {
            logger.info("Sending WhatsApp message to name: {}, phone: {}", studentContact.fullName(), maskPhone(studentContact.phoneNumber()));
            vacancyNotificationAdapter.notifyCandidate(
                    studentContact.phoneNumber(),
                    studentContact.fullName(),
                    matches.get(0).vacancy(),
                    matches.get(1).vacancy()
            );

            sendAdminNotificationEmail(studentContact);



            logger.info("WhatsApp message sent successfully to student: {}, phone: {}", studentContact.fullName(), maskPhone(studentContact.phoneNumber()));
        } catch (Exception e) {
            logger.error("Failed to send WhatsApp message for student: {}, error: {}", studentContact.fullName(), e.getMessage(), e);
            throw new RuntimeException("Failed to send WhatsApp message: " + e.getMessage(), e);
        }
    }

    private List<String> getVacancyIds(List<StudentVacancyMatch> matches) {
        return matches.stream()
                .filter(match -> !match.vacancy().id().isEmpty())
                .map(match -> match.vacancy().id())
                .toList();
    }

    private Student toStudent(StudentContact studentContact) {
        logger.debug("Embedding text for student: {}", studentContact.fullName());
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

    private void sendAdminNotificationEmail(StudentContact studentContact) {
        String emailBody = String.format(
                "Nieuwe student aangemeld:\n\nNaam: %s\nE-mail: %s\nTelefoonnummer: %s\nStudie: %s\nLocatie: %s",
                studentContact.fullName(),
                studentContact.email(),
                maskPhone(studentContact.phoneNumber()),
                studentContact.study(),
                studentContact.studyLocation()
        );

        emailService.send(
                "kevinkoot887@gmail.com",
                "Nieuwe student aangemeld",
                emailBody
        );

        logger.info("Confirmation email sent to kevinkoot887@gmail.com for student: {}", studentContact.fullName());
    }
}
