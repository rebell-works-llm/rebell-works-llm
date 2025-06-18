package com.rebellworksllm.backend.whatsapp.presentation.controller;

import com.rebellworksllm.backend.matching.presentation.HubSpotWebhookController;
import com.rebellworksllm.backend.whatsapp.config.WhatsAppCredentials;
import com.rebellworksllm.backend.whatsapp.presentation.dto.WhatsAppWebhookPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/whatsapp")
public class WhatsAppWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(HubSpotWebhookController.class);
    private static final Logger whatsappLogger = LoggerFactory.getLogger("WHATSAPP_LOGGER");

    private final WhatsAppCredentials whatsAppCredentials;

    public WhatsAppWebhookController(WhatsAppCredentials whatsAppCredentials) {
        this.whatsAppCredentials = whatsAppCredentials;
    }

    @GetMapping("/webhook")
    public ResponseEntity<String> verifyWebhook(
            @RequestParam(name = "hub.mode") String mode,
            @RequestParam(name = "hub.challenge") String challenge,
            @RequestParam(name = "hub.verify_token") String token) {

        logger.info("Incoming GET webhook params: {}, {}, {}", mode, challenge, token);

        if ("subscribe".equals(mode) && token.equals(whatsAppCredentials.getVerifyToken())) {
            return ResponseEntity.ok(challenge);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Verification failed");
        }
    }

    @PostMapping("/webhook")
    public void receiveWhatsAppPayload(@RequestBody WhatsAppWebhookPayload payload) {
        if (payload == null) {
            whatsappLogger.warn("Received null payload for WhatsApp webhook");
//            logger.warn("Fallback log: WhatsApp payload was null, logging random fallback");
        }

        whatsappLogger.info("Received WhatsApp payload: {}", payload);
    }
}
