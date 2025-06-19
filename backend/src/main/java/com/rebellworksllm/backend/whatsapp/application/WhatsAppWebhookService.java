package com.rebellworksllm.backend.whatsapp.application;

import com.rebellworksllm.backend.matching.application.StudentInterestHandlerService;
import com.rebellworksllm.backend.whatsapp.application.dto.ContactResponseMessage;
import com.rebellworksllm.backend.whatsapp.presentation.dto.WebhookPayload;
import com.rebellworksllm.backend.whatsapp.presentation.dto.WhatsAppMessagesData;
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

    public void processWebhook(WebhookPayload payload) {
        if (payload.entry() == null) return;

        for (WebhookPayload.Entry entry : payload.entry()) {
            if (entry.changes() == null) continue;

            for (WhatsAppMessagesData messagesData : entry.changes()) {
                if (!"messages".equals(messagesData.field())) continue;

                WhatsAppMessagesData.Value value = messagesData.value();
                if (value == null || value.contacts() == null) continue;

                // Map wa_id to name (for optional logging)
                Map<String, String> waIdToName = value.contacts().stream()
                        .collect(Collectors.toMap(WhatsAppMessagesData.Value.Contact::wa_id, c -> c.profile().name()));

                value.messages().stream()
                        .map(msg -> {
                            String from = "+" + msg.from();
                            String name = waIdToName.getOrDefault(msg.from(), "unknown");
                            String text = msg.text().body();
                            logger.info("Processing message from {} ({}): {}", name, from, text);
                            return new ContactResponseMessage(from, text);
                        })
                        .forEach(studentInterestHandlerService::handleReply);
            }
        }
    }
}
