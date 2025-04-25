package com.rebellworksllm.backend.whatsapp.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class WhatsAppService {

    private static final String PREPARED_API_URL = "https://graph.facebook.com/v22.0/%s/messages";

    private final String whatsAppToken;
    private final String whatsAppApiUrl;

    public WhatsAppService(@Value("${whatsapp.api.token}") String whatsAppToken,
                           @Value("${whatsapp.api.phoneNumberId}") String whatsAppNumberId) {
        this.whatsAppToken = whatsAppToken;
        this.whatsAppApiUrl = String.format(PREPARED_API_URL, whatsAppNumberId);
        System.out.println("Whatsapp API URL: " + whatsAppApiUrl);
    }

    public String sendWithVacancyTemplate(String phoneNumber,
                                          String name,
                                          String vac1,
                                          String vac2,
                                          String vac3,
                                          String vac4,
                                          String vac5) {
        try {
            String jsonBody = """
                    {
                      "messaging_product": "whatsapp",
                      "to": "%s",
                      "type": "template",
                      "template": {
                        "name": "vacancy_test",
                        "language": {
                          "code": "nl"
                        },
                        "components": [
                              {
                                "type": "body",
                                "parameters": [
                                    { "type": "text", "text": "%s" },
                                    { "type": "text", "text": "%s" },
                                    { "type": "text", "text": "%s" },
                                    { "type": "text", "text": "%s" },
                                    { "type": "text", "text": "%s" },
                                    { "type": "text", "text": "%s" }
                                ]
                              }
                            ]
                      }
                    }
                    """.formatted(phoneNumber, name, vac1, vac2, vac3, vac4, vac5);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(whatsAppApiUrl))
                    .header("Authorization", "Bearer " + whatsAppToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
