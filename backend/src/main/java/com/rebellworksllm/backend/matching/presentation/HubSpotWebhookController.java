package com.rebellworksllm.backend.matching.presentation;

import com.rebellworksllm.backend.matching.application.HubSpotWebhookService;
import com.rebellworksllm.backend.matching.presentation.dto.HubSpotWebhookPayload;
import com.rebellworksllm.backend.matching.presentation.dto.HubSpotWebhookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hubspot/contacts")
public class HubSpotWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(HubSpotWebhookController.class);

    private final HubSpotWebhookService webhookService;

    public HubSpotWebhookController(HubSpotWebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/created")
    public ResponseEntity<List<HubSpotWebhookResponse>> handleContactCreation(@RequestBody List<HubSpotWebhookPayload> payloads) {
        logger.info("Received HubSpot webhook with: {} payloads", payloads.size());
        List<HubSpotWebhookResponse> responses = new ArrayList<>();

        // Separate flow for each payload
        for (HubSpotWebhookPayload payload : payloads) {
            // correlation ID (link logs across matching flow)
            String correlationId = UUID.randomUUID().toString();
            MDC.put("correlationId", correlationId);
            // Start workflow for contact
            try {
                logger.info("Processing webhook for contact ID: {}, subscriptionType: {}", payload.objectId(), payload.subscriptionType());
                webhookService.processStudentMatch(payload.objectId());
                responses.add(new HubSpotWebhookResponse(HttpStatus.OK.value(), "StudentContact matched successfully for ID: " + payload.objectId()));
                logger.info("Successfully processed webhook for contact ID: {}", payload.objectId());
            } catch (Exception e) {
                logger.error("Error processing webhook for contact ID: {}", payload.objectId(), e);
                responses.add(new HubSpotWebhookResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to process webhook: " + e.getMessage()));
            } finally {
                MDC.clear();
            }
        }

        return ResponseEntity.ok(responses);
    }
}

