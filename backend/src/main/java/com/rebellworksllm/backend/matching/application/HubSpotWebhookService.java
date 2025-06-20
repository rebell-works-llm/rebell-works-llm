package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.email.application.EmailService;
import com.rebellworksllm.backend.hubspot.application.dto.StudentContact;
import com.rebellworksllm.backend.hubspot.application.HubSpotStudentProvider;
import com.rebellworksllm.backend.matching.application.exception.InsufficientMatchesException;
import com.rebellworksllm.backend.openai.domain.EmbeddingResult;
import com.rebellworksllm.backend.matching.domain.*;
import com.rebellworksllm.backend.openai.domain.OpenAIEmbeddingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.rebellworksllm.backend.matching.application.util.LogUtils.maskEmail;
import static com.rebellworksllm.backend.matching.application.util.LogUtils.maskPhone;

@Service
public class HubSpotWebhookService {

    private static final Logger logger = LoggerFactory.getLogger(HubSpotWebhookService.class);
    private static final int FIRST_MATCH_LIMIT = 5;

    private final StudentJobMatchEngine matchEngine;
    private final HubSpotStudentProvider studentProvider;
    private final OpenAIEmbeddingService embeddingService;
    private final EmailService emailService;
    private final VacancyNotificationAdapter vacancyNotificationAdapter;

    @Value("${mail.to}")
    private String mailTo;

    public HubSpotWebhookService(StudentJobMatchEngine matchEngine,
                                 HubSpotStudentProvider studentProvider,
                                 OpenAIEmbeddingService embeddingService,
                                 EmailService emailService,
                                 VacancyNotificationAdapter vacancyNotificationAdapter) {
        this.matchEngine = matchEngine;
        this.studentProvider = studentProvider;
        this.embeddingService = embeddingService;
        this.vacancyNotificationAdapter = vacancyNotificationAdapter;
        this.emailService = emailService;
    }

    @Async("jobMatchingExecutor")
    public void processStudentMatch(long id) {
        logger.info("Starting matching progress for ID: {}", id);
        StudentContact studentContact = studentProvider.getStudentById(id);
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
        String emailBody = String.format("""
                        📢 *Nieuwe student gematcht!*

                        👤 Naam: %s
                        📧 E-mail: %s
                        📱 Telefoon: %s
                        🎓 Studie: %s
                        📍 Locatie: %s

                        HubSpot object ID: %s
                        
                        Bekijk de student in HubSpot voor meer details.
                        """,
                studentContact.fullName(),
                studentContact.email(),
                maskPhone(studentContact.phoneNumber()),
                studentContact.study(),
                studentContact.studyLocation(),
                studentContact.id()
        );

        emailService.send(
                mailTo,
                "Nieuwe student gematcht",
                emailBody
        );

        logger.info("Confirmation email sent to {} for student: {}", maskEmail(mailTo), studentContact.fullName());
    }
}
