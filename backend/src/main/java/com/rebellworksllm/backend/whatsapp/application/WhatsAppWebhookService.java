package com.rebellworksllm.backend.whatsapp.application;

import com.rebellworksllm.backend.matching.application.StudentInterestHandlerService;
import com.rebellworksllm.backend.whatsapp.application.dto.ContactResponseMessage;
import com.rebellworksllm.backend.whatsapp.presentation.dto.Contact;
import com.rebellworksllm.backend.whatsapp.presentation.dto.WhatsAppWebhookPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WhatsAppWebhookService {

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppWebhookService.class);

    private final StudentInterestHandlerService studentInterestHandlerService;

    public WhatsAppWebhookService(StudentInterestHandlerService studentInterestHandlerService) {
        this.studentInterestHandlerService = studentInterestHandlerService;
    }

    public void processWebhook(WhatsAppWebhookPayload payload) {
        Map<String, String> waIdToNameMap = payload.value().contacts().stream()
                .collect(Collectors.toMap(Contact::wa_id, c -> c.profile().name()));

        payload.value().messages().stream()
                .map(msg -> {
                    String waId = msg.from();
                    String name = waIdToNameMap.getOrDefault(waId, "unknown");
                    String text = msg.text().body();

                    logger.info("Processing message from {}: {}", name, text);

                    return new ContactResponseMessage("+" + waId, text);
                })
                .forEach(studentInterestHandlerService::handleReply);
    }

}
