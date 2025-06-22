package com.rebellworksllm.backend.modules.matching.presentation;

import com.rebellworksllm.backend.modules.matching.application.HubSpotWebhookService;
import com.rebellworksllm.backend.modules.matching.application.dto.BatchResponse;
import com.rebellworksllm.backend.modules.matching.presentation.dto.HubSpotWebhooksBatchResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<BatchResponse> handleContactCreation(@Valid @RequestBody List<HubSpotWebhooksBatchResponse.HubSpotWebhooksPayload> response) {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        logger.info("Incoming webhook batch with {} payload(s)", response.size());

        BatchResponse batchResponse = webhookService.processBatch(response);

        MDC.clear();
        return ResponseEntity.ok().body(batchResponse);
    }
}

