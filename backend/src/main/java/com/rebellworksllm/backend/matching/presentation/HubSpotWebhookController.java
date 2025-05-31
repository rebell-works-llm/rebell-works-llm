package com.rebellworksllm.backend.matching.presentation;

import com.rebellworksllm.backend.matching.application.HubSpotWebhookService;
import com.rebellworksllm.backend.matching.presentation.dto.HubSpotWebhookPayload;
import com.rebellworksllm.backend.matching.presentation.dto.HubSpotWebhookResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<HubSpotWebhookResponse> handleContactCreation(@Valid @RequestBody HubSpotWebhookPayload payload) {
        // correlation ID (link logs across matching flow)
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);

        logger.info("Received HubSpot webhook for contact ID: {}", payload.objectId());
        webhookService.processStudentMatch(payload.objectId());
        HubSpotWebhookResponse response = new HubSpotWebhookResponse(
                HttpStatus.OK.value(),
                "StudentContact matched successfully");
        logger.info("Successfully processed webhook for contact ID: {}", payload.objectId());
        return ResponseEntity.ok(response);
    }
}

