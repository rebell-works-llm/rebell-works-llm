package com.rebellworksllm.backend.whatsapp.presentation.controller;

import com.rebellworksllm.backend.whatsapp.presentation.dto.WhatsAppWebhookPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/whatsapp")
public class WhatsAppWebhookController {

    //    private static final Logger logger = LoggerFactory.getLogger(HubSpotWebhookController.class);
    private static final Logger whatsappLogger = LoggerFactory.getLogger("WHATSAPP_LOGGER");

    @PostMapping("/webhook")
    public void receiveWhatsAppPayload(@RequestBody WhatsAppWebhookPayload payload) {
        if (payload == null) {
            whatsappLogger.warn("Received null payload for WhatsApp webhook");
//            logger.warn("Fallback log: WhatsApp payload was null, logging random fallback");
        }

        whatsappLogger.info("Received WhatsApp payload: {}", payload);
    }
}
