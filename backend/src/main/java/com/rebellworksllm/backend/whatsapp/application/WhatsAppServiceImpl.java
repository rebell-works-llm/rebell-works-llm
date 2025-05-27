package com.rebellworksllm.backend.whatsapp.application;

import com.rebellworksllm.backend.whatsapp.config.WhatsAppCredentials;
import com.rebellworksllm.backend.whatsapp.domain.WhatsAppService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class WhatsAppServiceImpl implements WhatsAppService {

    private final WhatsAppCredentials credentials;

    public WhatsAppServiceImpl(WhatsAppCredentials credentials) {
        this.credentials = credentials;
    }

    public void sendWithVacancyTemplate(String phoneNumber,
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
                    .uri(URI.create(credentials.getApiBaseUrl() + credentials.getPhoneNumberId() + "/messages"))
                    .header("Authorization", "Bearer " + credentials.getApiKey())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            try (HttpClient client = HttpClient.newHttpClient()) {
                client.send(request, HttpResponse.BodyHandlers.ofString());
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
