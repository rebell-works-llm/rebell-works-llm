package com.rebellworksllm.backend.whatsapp.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rebellworksllm.backend.whatsapp.config.WhatsAppCredentials;
import com.rebellworksllm.backend.whatsapp.domain.WhatsAppService;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Service
public class WhatsAppServiceImpl implements WhatsAppService {

    private final WhatsAppCredentials credentials;
    private final ObjectMapper objectMapper;

    public WhatsAppServiceImpl(WhatsAppCredentials credentials) {
        this.credentials = credentials;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void sendTemplateMessage(String phoneNumber,
                                    String templateName,
                                    String languageCode,
                                    List<String> parameters) {

        try {

            List<Map<String, Object>> bodyParams = parameters.stream()
                    .map(this::checkAndCleanText)
                    .map(text -> Map.<String, Object>of("type", "text", "text", text))
                    .toList();

            Map<String, Object> messageBody = Map.of(
                    "messaging_product", "whatsapp",
                    "to", phoneNumber,
                    "type", "template",
                    "template", Map.of(
                            "name", templateName,
                            "language", Map.of("code", languageCode),
                            "components", List.of(
                                    Map.of(
                                            "type", "body",
                                            "parameters", bodyParams
                                    )
                            )
                    )
            );

            String jsonBody = objectMapper.writeValueAsString(messageBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(credentials.getApiBaseUrl() + credentials.getPhoneNumberId() + "/messages"))
                    .header("Authorization", "Bearer " + credentials.getApiKey())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("WhatsApp API error: " + response.body());
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Fout bij het versturen van WhatsApp-bericht", e);
        }
    }

    private String checkAndCleanText(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "Dit veld is nog unknown at the moment";
        }
        String cleaned = input.replaceAll("[\\n\\t\\r]+", " ")
                .replaceAll(" {2,}", " ")
                .trim();
        return StringEscapeUtils.escapeJson(cleaned);
    }
}
