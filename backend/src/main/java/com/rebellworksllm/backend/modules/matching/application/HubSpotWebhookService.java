package com.rebellworksllm.backend.modules.matching.application;

import com.rebellworksllm.backend.modules.email.application.EmailService;
import com.rebellworksllm.backend.modules.hubspot.application.dto.StudentContact;
import com.rebellworksllm.backend.modules.hubspot.application.HubSpotStudentProvider;
import com.rebellworksllm.backend.modules.matching.application.dto.BatchResponse;
import com.rebellworksllm.backend.modules.matching.application.util.MatchingUtils;
import com.rebellworksllm.backend.modules.matching.data.MatchMessageRepository;
import com.rebellworksllm.backend.modules.matching.data.dto.MatchMessageRequest;
import com.rebellworksllm.backend.modules.matching.presentation.dto.HubSpotWebhooksBatchResponse;
import com.rebellworksllm.backend.modules.matching.domain.Student;
import com.rebellworksllm.backend.modules.matching.domain.StudentVacancyMatch;
import com.rebellworksllm.backend.modules.matching.domain.Vacancy;
import com.rebellworksllm.backend.modules.openai.application.dto.EmbeddingResult;
import com.rebellworksllm.backend.modules.openai.application.OpenAIEmbeddingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Value("${mail.to.student-matched}")
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
                                    return BatchResponse.singleMessage(payload.objectId(), "Failed: " + ex.getMessage());
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

        List<BatchResponse.BatchPayloadStepResult> stepResults = new ArrayList<>();

        try {
            logger.info("Processing webhook for objectId: {}", id);

            // 1. Student
            Student student;
            try {
                student = findStudent(id);
                stepResults.add(new BatchResponse.BatchPayloadStepResult(
                        "student", true, "Student fetched and embedding created"));
            } catch (Exception e) {
                logger.error("Failed to fetch student or embedding for id {}: {}", id, e.getMessage(), e);
                stepResults.add(new BatchResponse.BatchPayloadStepResult(
                        "student", false, "Student fetch/embedding failed: " + e.getMessage()));
                return new BatchResponse.BatchPayloadResponse(id, stepResults);
            }

            List<StudentVacancyMatch> matches;
            try {
                matches = matchEngine.query(student, FIRST_MATCH_LIMIT);
                if (matches.size() < FIRST_MATCH_LIMIT) {
                    String msg = "Insufficient vacancy matches found for student: " + id;
                    logger.warn(msg);
                    stepResults.add(new BatchResponse.BatchPayloadStepResult("matching", false, msg));
                    return new BatchResponse.BatchPayloadResponse(id, stepResults);
                }
                stepResults.add(new BatchResponse.BatchPayloadStepResult("matching", true, "Matches found: " + matches.size()));
            } catch (Exception e) {
                logger.error("Matching failed for {}: {}", student.name(), e.getMessage(), e);
                stepResults.add(new BatchResponse.BatchPayloadStepResult("matching", false, "Matching failed: " + e.getMessage()));
                return new BatchResponse.BatchPayloadResponse(id, stepResults);
            }

            // 2. WhatsApp
            try {
                sendVacancyNotifications(student, matches);
                stepResults.add(new BatchResponse.BatchPayloadStepResult("whatsapp", true, "WhatsApp notification sent"));
            } catch (Exception e) {
                logger.error("WhatsApp notification failed for {}: {}", student.name(), e.getMessage(), e);
                stepResults.add(new BatchResponse.BatchPayloadStepResult("whatsapp", false, "WhatsApp failed: " + e.getMessage()));
            }

            // 3. Persist match
            try {
                persistMatchMessages(student, matches);
                stepResults.add(new BatchResponse.BatchPayloadStepResult("persist", true, "Match message persisted"));
            } catch (Exception e) {
                logger.error("Persisting match message failed for {}: {}", student.name(), e.getMessage(), e);
                stepResults.add(new BatchResponse.BatchPayloadStepResult("persist", false, "Persist failed: " + e.getMessage()));
            }

            // 4. Admin mail
            try {
                sendAdminNotificationEmail(student);
                stepResults.add(new BatchResponse.BatchPayloadStepResult("adminMail", true, "Admin mail sent"));
            } catch (Exception e) {
                logger.error("Admin mail failed for {}: {}", student.name(), e.getMessage(), e);
                stepResults.add(new BatchResponse.BatchPayloadStepResult("adminMail", false, "Admin mail failed: " + e.getMessage()));
            }

            logger.info("Processed webhook for objectId: {} with results: {}", id, stepResults);
            return new BatchResponse.BatchPayloadResponse(id, stepResults);
        } catch (Exception ex) {
            logger.error("Failed to process webhook - objectId: {}, error: {}", id, ex.getMessage(), ex);
            return BatchResponse.singleMessage(id, ex.getMessage());
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
