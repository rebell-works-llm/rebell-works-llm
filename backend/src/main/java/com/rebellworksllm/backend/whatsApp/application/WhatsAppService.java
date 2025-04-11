package com.rebellworksllm.backend.whatsApp.application;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WhatsAppService {

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
        },
        "components": [
          {
            "type": "body",
            "parameters": [
              {
                "type": "text",
                "text": "%s"
              }
            ]
          }
        ]
      }
    }
    """.formatted("31657771880", "hoi");

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
