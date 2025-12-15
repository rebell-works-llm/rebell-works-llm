package com.rebellworksllm.backend.modules.matching.application;

import com.rebellworksllm.backend.common.utils.ErrorNotificationService;
import com.rebellworksllm.backend.modules.email.application.EmailService;
import com.rebellworksllm.backend.modules.hubspot.application.dto.StudentContact;
import com.rebellworksllm.backend.modules.hubspot.application.HubSpotStudentProvider;
import com.rebellworksllm.backend.modules.matching.application.dto.BatchResponse;
import com.rebellworksllm.backend.modules.matching.application.exception.MatchingException;
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
    private final ErrorNotificationService errorNotificationService;

    @Value("${mail.to.student-matched}")
    private String mailTo;

    public HubSpotWebhookService(
            StudentJobMatchEngine matchEngine,
            HubSpotStudentProvider studentProvider,
            OpenAIEmbeddingService embeddingService,
            EmailService emailService,
            VacancyNotificationAdapter vacancyNotificationAdapter,
            @Qualifier("jobMatchingExecutor") Executor jobMatchingExecutor,
            MatchMessageRepository matchMessageRepository,
            ErrorNotificationService errorNotificationService
    ) {
        this.matchEngine = matchEngine;
        this.studentProvider = studentProvider;
        this.embeddingService = embeddingService;
        this.vacancyNotificationAdapter = vacancyNotificationAdapter;
        this.emailService = emailService;
        this.jobMatchingExecutor = jobMatchingExecutor;
        this.matchMessageRepository = matchMessageRepository;
        this.errorNotificationService = errorNotificationService;
    }

    public BatchResponse processBatch(
            List<HubSpotWebhooksBatchResponse.HubSpotWebhooksPayload> payloads
    ) {
        logger.info("Received batch with {} payload(s)", payloads.size());

        try {
            runBatchItemsInParallel(payloads);
            return BatchResponse.accepted();

        } catch (Exception ex) {
            logger.error("Failed to start batch processing", ex);
            return BatchResponse.failed();
        }
    }

    private void runBatchItemsInParallel(List<HubSpotWebhooksBatchResponse.HubSpotWebhooksPayload> payloads) {
        payloads.forEach(payload ->
                CompletableFuture.runAsync(
                        () -> runMatchingWorkflow(payload.objectId()),
                        jobMatchingExecutor
                )
        );
    }

    public void runMatchingWorkflow(long objectId) {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);

        try {
            logger.info("Starting matching workflow for objectId={}", objectId);

            final Student student = findStudent(objectId);
            final List<StudentVacancyMatch> matches = findMatches(student, objectId);
            sendVacancyNotifications(student, matches);
            persistMatchMessages(student, matches);

            try {
                sendAdminNotificationEmail(student);
            } catch (Exception mailEx) {
                logger.warn("Admin mail failed for student={}, reason={}", student.id(), mailEx.getMessage(), mailEx);
            }

            logger.info("Matching workflow completed successfully for objectId={}", objectId);

        } catch (Exception ex) {
            handleWorkflowFailure(objectId, correlationId, ex);
            throw new MatchingException("Matching workflow failed for objectId=" + objectId, ex);
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

    private List<StudentVacancyMatch> findMatches(Student student, long objectId) {
        List<StudentVacancyMatch> matches = matchEngine.query(student, FIRST_MATCH_LIMIT);

        if (matches.size() < FIRST_MATCH_LIMIT) {
            throw new MatchingException("Insufficient matches for student " + objectId);
        }

        return matches;
    }

    private void sendVacancyNotifications(Student student, List<StudentVacancyMatch> matches) {
        if (matches.size() < 2) {
            throw new MatchingException("Not enough matches to notify student " + student.id());
        }

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

    private void handleWorkflowFailure(long objectId, String correlationId, Exception ex) {
        logger.error("Matching workflow FAILED for objectId={}, correlationId={}", objectId, correlationId, ex);

        errorNotificationService.sendErrorEmail(
                "HubSpot Matching Workflow Failed",
                """
                        ObjectId: %s
                        CorrelationId: %s
                        Error: %s
                        """.formatted(objectId, correlationId, ex.getMessage()),
                ex
        );
    }
}
