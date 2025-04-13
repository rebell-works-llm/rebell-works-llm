package com.rebellworksllm.backend.whatsApp.application;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
@Service
public class WhatsAppService {


    @PostConstruct
    public void sendFirstMessageAtStartup() throws IOException, InterruptedException {
        String telefoonNummer = "31657771880";
        sendFirstMessage(telefoonNummer);
    }

    public void sendFirstMessage(String telefoonNummer) throws IOException, InterruptedException{
        String token = EnvLoader.getWhatsAppToken();
        String numberID = EnvLoader.getPhoneNumberId();

        String url = "https://graph.facebook.com/v17.0/" + numberID + "/messages";
        String jsonBody = """
    {
      "messaging_product": "whatsapp",
      "to": "%s",
      "type": "template",
      "template": {
        "name": "hello_world",
        "language": {
          "code": "en_US"
        }
      }
    }
    """.formatted(telefoonNummer);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Response: " + response.body());


    }
}
