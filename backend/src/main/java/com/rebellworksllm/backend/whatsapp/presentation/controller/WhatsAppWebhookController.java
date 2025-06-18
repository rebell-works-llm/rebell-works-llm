package com.rebellworksllm.backend.whatsapp.presentation.controller;

import com.rebellworksllm.backend.whatsapp.presentation.dto.WhatsAppWebhookPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/whatsapp")
public class WhatsAppWebhookController {

    @PostMapping("/webhook")
    public ResponseEntity<WhatsAppWebhookPayload> receiveWhatsAppPayload(@RequestBody WhatsAppWebhookPayload payload) {
        return ResponseEntity.ok(payload);
    }
}
