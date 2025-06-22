package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.email.application.EmailService;
import com.rebellworksllm.backend.hubspot.application.dto.StudentContact;
import com.rebellworksllm.backend.hubspot.application.HubSpotStudentProvider;
import com.rebellworksllm.backend.matching.application.dto.BatchResponse;
import com.rebellworksllm.backend.matching.application.exception.InsufficientMatchesException;
import com.rebellworksllm.backend.matching.application.util.MatchingUtils;
import com.rebellworksllm.backend.matching.data.MatchMessageRepository;
import com.rebellworksllm.backend.matching.data.dto.MatchMessageRequest;
import com.rebellworksllm.backend.matching.presentation.dto.HubSpotWebhooksBatchResponse;
import com.rebellworksllm.backend.openai.domain.EmbeddingResult;
import com.rebellworksllm.backend.matching.domain.*;
import com.rebellworksllm.backend.openai.domain.OpenAIEmbeddingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static com.rebellworksllm.backend.common.utils.LogUtils.maskEmail;
import static com.rebellworksllm.backend.common.utils.LogUtils.maskPhone;

@Service
public class HubSpotWebhookService {

    private static final Logger logger = LoggerFactory.getLogger(HubSpotWebhookService.class);
    private static final int FIRST_MATCH_LIMIT = 4;

    private final Executor jobMatchingExecutor;
    private final StudentJobMatchEngine matchEngine;
    private final HubSpotStudentProvider studentProvider;
    private final OpenAIEmbeddingService embeddingService;
    private final EmailService emailService;
    private final VacancyNotificationAdapter vacancyNotificationAdapter;
    private final MatchMessageRepository matchMessageRepository;

    @Value("${mail.to}")
    private String mailTo;

    public HubSpotWebhookService(StudentJobMatchEngine matchEngine,
                                 HubSpotStudentProvider studentProvider,
                                 OpenAIEmbeddingService embeddingService,
                                 EmailService emailService,
                                 VacancyNotificationAdapter vacancyNotificationAdapter,
                                 @Qualifier("jobMatchingExecutor") Executor jobMatchingExecutor,
                                 MatchMessageRepository matchMessageRepository) {
        this.matchEngine = matchEngine;
        this.studentProvider = studentProvider;
        this.embeddingService = embeddingService;
        this.vacancyNotificationAdapter = vacancyNotificationAdapter;
        this.emailService = emailService;
        this.jobMatchingExecutor = jobMatchingExecutor;
        this.matchMessageRepository = matchMessageRepository;
    }

    public BatchResponse processBatch(List<HubSpotWebhooksBatchResponse.HubSpotWebhooksPayload> payloads) {
        logger.info("Starting batch processing of {} payload(s)", payloads.size());

        // Launch each item in parallel
        List<CompletableFuture<BatchResponse.BatchPayloadResponse>> futures = payloads.stream()
                .map(payload ->
                        CompletableFuture.supplyAsync(() -> processStudentMatch(payload.objectId()), jobMatchingExecutor)
                                .exceptionally(ex -> {
                                    logger.error("Batch error for objectId={}: {}", payload.objectId(), ex.getMessage(), ex);
                                    return new BatchResponse.BatchPayloadResponse(payload.objectId(), "Failed: " + ex.getMessage());
                                })
                )
                .toList();

        // Wait for all tasks and collect results
        List<BatchResponse.BatchPayloadResponse> results = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream().map(CompletableFuture::join).toList())
                .join();

        logger.info("Completed batch processing for {} payload(s)", results.size());
        return BatchResponse.success("Batch processed", results);
    }

    public BatchResponse.BatchPayloadResponse processStudentMatch(long id) {
        final String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);

        try {
            logger.info("Processing webhook for objectId: {}", id);

            Student student = findStudent(id);
            List<StudentVacancyMatch> matches = matchEngine.query(student, FIRST_MATCH_LIMIT);

            if (matches.size() < FIRST_MATCH_LIMIT) {
                logger.warn("Insufficient matches for student {}: required={}, found={}", id, FIRST_MATCH_LIMIT, matches.size());
                throw new InsufficientMatchesException("Insufficient vacancy matches found for student: " + id);
            }

            sendVacancyNotifications(student, matches);
            persistMatchMessages(student, matches);
            sendAdminNotificationEmail(student);

            logger.info("Processed webhook successfully for objectId: {}", id);
            return new BatchResponse.BatchPayloadResponse(id, "Student matched successfully");
        } catch (Exception ex) {
            logger.error("Failed to process webhook - objectId: {}, error: {}", id, ex.getMessage(), ex);
            return new BatchResponse.BatchPayloadResponse(id, ex.getMessage());
        } finally {
            MDC.clear();
        }
    }

    private Student findStudent(long id) {
        StudentContact studentContact = studentProvider.getStudentById(id);
        EmbeddingResult studentEmbeddingResult = embeddingService.embedText(studentContact.stringify());

        return new Student(
                studentContact.id(),
                studentContact.fullName(),
                studentContact.email(),
                studentContact.phoneNumber(),
                studentContact.study(),
                studentContact.text(),
                studentContact.studyLocation(),
                studentEmbeddingResult
        );
    }

    private void sendVacancyNotifications(Student student, List<StudentVacancyMatch> matches) {
        Vacancy vac1 = matches.get(0).vacancy();
        Vacancy vac2 = matches.get(1).vacancy();
        vacancyNotificationAdapter.notifyCandidate(student.phoneNumber(), student.name(), vac1, vac2);
        logger.info("WhatsApp notification sent to student: {}, phone: {}", student.name(), maskPhone(student.phoneNumber()));
    }

    private void persistMatchMessages(Student student, List<StudentVacancyMatch> matches) {
        List<String> vacancyIds = matches.stream().limit(4).map(m -> m.vacancy().id()).toList();
        String normalizedPhone = MatchingUtils.normalizePhone(student.phoneNumber());
        matchMessageRepository.save(new MatchMessageRequest(vacancyIds, normalizedPhone));
    }

    private void sendAdminNotificationEmail(Student student) {
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
                student.name(),
                student.email(),
                maskPhone(student.phoneNumber()),
                student.study(),
                student.studyLocation(),
                student.id()
        );

        emailService.send(mailTo, "Nieuwe student gematcht", emailBody);
        logger.info("Confirmation email sent to {} for student: {}", maskEmail(mailTo), student.name());
    }
}
