package com.rebellworksllm.backend.whatsapp.presentation.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rebellworksllm.backend.whatsapp.application.WhatsAppWebhookService;
import com.rebellworksllm.backend.whatsapp.config.WhatsAppCredentials;
import com.rebellworksllm.backend.whatsapp.presentation.dto.WebhookPayload;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/whatsapp/webhook")
public class WhatsAppWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppWebhookController.class);

    private final WhatsAppCredentials whatsAppCredentials;
    private final WhatsAppWebhookService whatsAppWebhookService;

    public WhatsAppWebhookController(WhatsAppCredentials whatsAppCredentials, WhatsAppWebhookService whatsAppWebhookService) {
        this.whatsAppCredentials = whatsAppCredentials;
        this.whatsAppWebhookService = whatsAppWebhookService;
    }

    @GetMapping
    public ResponseEntity<String> verifyWebhook(
            @RequestParam(name = "hub.mode") String mode,
            @RequestParam(name = "hub.challenge") String challenge,
            @RequestParam(name = "hub.verify_token") String token) {

        if ("subscribe".equals(mode) && token.equals(whatsAppCredentials.getVerifyToken())) {
            return ResponseEntity.ok(challenge);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Verification failed");
        }
    }

    @PostMapping
    public ResponseEntity<Void> receive(@RequestBody WebhookPayload payload) {
        logger.info("Received webhook payload for object: {}", payload.object());
        whatsAppWebhookService.processWebhook(payload);
        return ResponseEntity.ok().build();
    }
}
