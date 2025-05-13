package com.rebellworksllm.backend.matching.presentation;

import com.rebellworksllm.backend.matching.application.HubSpotWebhookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hubspot/contacts")
public class HubSpotWebhookController {

    private final HubSpotWebhookService webhookService;

    public HubSpotWebhookController(HubSpotWebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/created")
    public ResponseEntity<HubSpotWebhookResponse> handleContactCreation(@Valid @RequestBody HubSpotWebhookPayload payload) {
        webhookService.startMatchEventForObject(payload.objectId());
        HubSpotWebhookResponse response = new HubSpotWebhookResponse(
                HttpStatus.OK.value(),
                "Student matched successfully"
        );
        return ResponseEntity.ok(response);
    }
}

