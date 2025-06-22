package com.rebellworksllm.backend.modules.whatsapp.presentation.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rebellworksllm.backend.modules.whatsapp.application.WhatsAppWebhookService;
import com.rebellworksllm.backend.modules.whatsapp.application.exception.MissingPayloadFieldException;
import com.rebellworksllm.backend.modules.whatsapp.config.WhatsAppCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/whatsapp/webhook")
public class WhatsAppWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppWebhookController.class);
    private static final ObjectMapper mapper = new ObjectMapper();

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
    public ResponseEntity<String> receive(@RequestBody JsonNode payload) {
        try {
            logger.info("Received WhatsApp webhook (raw): {}", payload);

            whatsAppWebhookService.processWebhook(payload);

            return ResponseEntity.ok().build();
        } catch (MissingPayloadFieldException e) {
            logger.error("Failed to process WhatsApp webhook: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to process webhook: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process webhook: " + e.getMessage());
        }
    }
}
