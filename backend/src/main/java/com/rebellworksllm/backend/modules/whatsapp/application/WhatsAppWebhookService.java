package com.rebellworksllm.backend.modules.whatsapp.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.rebellworksllm.backend.modules.matching.application.StudentInterestHandlerService;
import com.rebellworksllm.backend.modules.whatsapp.application.exception.MissingPayloadFieldException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppWebhookService {

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppWebhookService.class);

    private final StudentInterestHandlerService studentInterestHandlerService;

    public WhatsAppWebhookService(StudentInterestHandlerService studentInterestHandlerService) {
        this.studentInterestHandlerService = studentInterestHandlerService;
    }

    public void processWebhook(JsonNode payload) {
        if (!payload.has("entry")) {
            logger.warn("Payload missing 'entry' field");
            throw new MissingPayloadFieldException("Payload missing 'entry' field");
        }

        for (JsonNode entry : payload.get("entry")) {
            if (!entry.has("changes")) continue;

            for (JsonNode change : entry.get("changes")) {
                String field = change.path("field").asText();
                JsonNode value = change.path("value");

                if (field.equals("messages")) {
                    logger.info("Dynamically processing 'messages' payload");
                    processMessages(value);
                }

                logger.warn("Unknown field '{}' with value: {}", field, value);
            }
        }
    }

    private void processMessages(JsonNode value) {
        JsonNode contacts = value.path("contacts");
        String contactName = contacts.isArray() && !contacts.isEmpty()
                ? contacts.get(0).path("profile").path("name").asText("unknown")
                : "unknown";

        JsonNode messages = value.path("messages");
        if (messages.isArray()) {
            for (JsonNode msg : messages) {
                processMessage(msg, contactName);
            }
        }
    }

    private void processMessage(JsonNode msg, String contactName) {
        String msgType = msg.path("type").asText();
        String from = msg.path("from").asText();
        String logId = msg.path("id").asText();

        if (msgType.equals("button")) {
            String payload = msg.path("button").path("payload").asText(null);
            String btnText = msg.path("button").path("text").asText(null);
            logger.info("BUTTON from {} ({}): payload={}, text={}", contactName, from, payload, btnText);
        }
    }
}
