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

import static com.rebellworksllm.backend.matching.application.util.LogUtils.maskEmail;
import static com.rebellworksllm.backend.matching.application.util.LogUtils.maskPhone;

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

        // Launch each item in parallel
        List<CompletableFuture<BatchResponse.BatchPayloadResponse>> futures = payloads.stream()
                .map(payload ->
                        CompletableFuture.supplyAsync(() -> processStudentMatch(payload.objectId()), jobMatchingExecutor)
                                .exceptionally(ex -> {
                                    logger.error("Error processing objectId={}: {}", payload.objectId(), ex.getMessage(), ex);
                                    return new BatchResponse.BatchPayloadResponse(payload.objectId(), "Failed: " + ex.getMessage());
                                })
                )
                .toList();

        // Wait for all tasks to finish
        CompletableFuture<Void> allDone = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        // Collect results (blocking here until all are done, still async up to this point!)
        List<BatchResponse.BatchPayloadResponse> results = allDone.thenApply(v ->
                futures.stream().map(CompletableFuture::join).toList()
        ).join();

        logger.info("Completed batch processing for {} payload(s)", results.size());
        return BatchResponse.success("Batch processed", results);
    }

    public BatchResponse.BatchPayloadResponse processStudentMatch(long id) {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        logger.info("Processing webhook - objectId: {}", id);

        try {
            Student student = findStudent(id);
            List<StudentVacancyMatch> matches = matchEngine.query(student, FIRST_MATCH_LIMIT);

            if (matches.size() < FIRST_MATCH_LIMIT) {
                logger.warn("Insufficient matches found for student: {}, required: {}, found: {}", id, FIRST_MATCH_LIMIT, matches.size());
                throw new InsufficientMatchesException("Insufficient vacancy matches found for student: " + id);
            }

            try {
                logger.info("Sending WhatsApp message to student: {}, phone: {}", id, maskPhone(student.phoneNumber()));

                Vacancy vac1 = matches.get(0).vacancy();
                Vacancy vac2 = matches.get(1).vacancy();
                Vacancy vac3 = matches.get(1).vacancy();
                Vacancy vac4 = matches.get(1).vacancy();
                vacancyNotificationAdapter.notifyCandidate(
                        student.phoneNumber(),
                        student.name(),
                        vac1,
                        vac2
                );

                List<String> vacancyIds = List.of(vac1.id(), vac2.id(), vac3.id(), vac4.id());
                String normalizedPhone = MatchingUtils.normalizePhone(student.phoneNumber());
                matchMessageRepository.save(new MatchMessageRequest(vacancyIds, normalizedPhone));

                sendAdminNotificationEmail(student);

                logger.info("WhatsApp message sent successfully to student: {}, phone: {}", student.name(), maskPhone(student.phoneNumber()));
                logger.debug("Successfully processed webhook for objectId: {}", id);
            } catch (Exception e) {
                logger.error("Failed to send WhatsApp message for student: {}, error: {}", student.name(), e.getMessage(), e);
                throw new RuntimeException("Failed to send WhatsApp message: " + e.getMessage(), e);
            }
        } catch (Exception ex) {
            logger.error("Failed to process webhook - objectId: {}, error: {}", id, ex.getMessage(), ex);
            return new BatchResponse.BatchPayloadResponse(id, ex.getMessage());
        }

        MDC.clear();
        return new BatchResponse.BatchPayloadResponse(id, "Student matched successfully");
    }

    private Student findStudent(long id) {
        StudentContact studentContact = studentProvider.getStudentById(id);
        logger.debug("Fetched HubSpot student: {}", studentContact.fullName());

        logger.debug("Embedding text for student: {}", studentContact.fullName());
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
