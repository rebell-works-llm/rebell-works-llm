package com.rebellworksllm.backend.matching.presentation;

import com.rebellworksllm.backend.matching.application.HubSpotWebhookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hubspot/contacts")
public class HubSpotWebhookController {

    private static final int FIRST_MATCH_LIMIT = 5;

    private final HubSpotWebhookService webhookService;

    public HubSpotWebhookController(HubSpotWebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/created")
    public ResponseEntity<HubSpotWebhookResponse> handleContactCreation(@RequestBody HubSpotWebhookPayload payload) {
        webhookService.matchStudent(payload.objectId(), FIRST_MATCH_LIMIT);
        HubSpotWebhookResponse response = new HubSpotWebhookResponse(
                Integer.parseInt(HttpStatus.OK.toString()),
                "Student matched successfully"
        );

        return ResponseEntity.ok(response);
    }
}

