package com.rebellworksllm.backend.modules.whatsapp.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rebellworksllm.backend.common.utils.LogUtils;
import com.rebellworksllm.backend.common.utils.TextUtils;
import com.rebellworksllm.backend.modules.whatsapp.application.exception.WhatsAppException;
import com.rebellworksllm.backend.modules.whatsapp.config.WhatsAppCredentials;
import com.rebellworksllm.backend.modules.whatsapp.domain.WhatsAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

@Service
public class WhatsAppServiceImpl implements WhatsAppService {

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppServiceImpl.class);

    private final WhatsAppCredentials credentials;
    private final ObjectMapper objectMapper;

    public WhatsAppServiceImpl(WhatsAppCredentials credentials) {
        this.credentials = credentials;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void sendTemplateMessage(
            final String phoneNumber,
            final String templateName,
            final String languageCode,
            final List<String> parameters
    ) {
        final String maskedPhone = LogUtils.maskPhone(phoneNumber);

        logger.info("Preparing to send WhatsApp message to {} with template '{}'", maskedPhone, templateName);

        try {
            if (templateName == null || languageCode == null || parameters == null) {
                logger.error("Cannot send WhatsApp message: null input detected (phoneNumber={}, templateName={}, languageCode={}, parameters={})",
                        maskedPhone, templateName, languageCode, parameters);
                throw new WhatsAppException("Input parameters must not be null");
            }

            Map<String, Object> templateMap = new HashMap<>();
            templateMap.put("name", templateName);
            templateMap.put("language", Map.of("code", languageCode));


            if (!parameters.isEmpty()) {
                List<Map<String, Object>> bodyParams = parameters.stream()
                        .map(TextUtils::checkAndCleanText)
                        .map(text -> Map.<String, Object>of("type", "text", "text", text))
                        .toList();

                templateMap.put("components", List.of(
                        Map.of("type", "body", "parameters", bodyParams)
                ));
            }

            Map<String, Object> messageBody = Map.of(
                    "messaging_product", "whatsapp",
                    "to", phoneNumber,
                    "type", "template",
                    "template", templateMap
            );

            String jsonBody = objectMapper.writeValueAsString(messageBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(credentials.getApiBaseUrl() + credentials.getPhoneNumberId() + "/messages"))
                    .header("Authorization", "Bearer " + credentials.getApiKey())
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(15))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            logger.debug("Sending WhatsApp HTTP request to {} with template '{}'", maskedPhone, templateName);

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            if (statusCode >= 400) {
                logger.error("WhatsApp API error (status: {}) when sending to {}. Response: {}", statusCode, maskedPhone, response.body());
                throw new WhatsAppException("WhatsApp API error (status: " + statusCode + "): " + response.body());
            }

            logger.info("Successfully sent WhatsApp message to {} with template '{}'. Status: {}", maskedPhone, templateName, statusCode);
            logger.debug("WhatsApp API response for {}: {}", maskedPhone, response.body());
        } catch (IOException | InterruptedException ex) {
            logger.error("Technical error sending WhatsApp message to {}: {}", maskedPhone, ex.getMessage(), ex);
            Thread.currentThread().interrupt();
            throw new WhatsAppException("Error sending WhatsApp message to " + maskedPhone, ex);
        } catch (Exception ex) {
            logger.error("Unexpected error sending WhatsApp message to {}: {}", maskedPhone, ex.getMessage(), ex);
            throw new WhatsAppException("Unexpected error sending WhatsApp message to " + maskedPhone, ex);
        }
    }
}
