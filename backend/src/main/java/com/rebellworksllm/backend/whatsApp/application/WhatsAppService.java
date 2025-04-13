package com.rebellworksllm.backend.whatsApp.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rebellworksllm.backend.matching.application.dto.QueryResponseDto;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
@Service
public class WhatsAppService {




    public void sendFirstMessage(String telefoonNummer) throws IOException, InterruptedException{
        String token = EnvLoader.getWhatsAppToken();
        String numberID = EnvLoader.getPhoneNumberId();

        String responseJson = fetchQueryResponseJson();

        ObjectMapper objectMapper = new ObjectMapper();
        QueryResponseDto responseDto = objectMapper.readValue(responseJson, QueryResponseDto.class);

        String websiteLink = "-";
        if (responseDto.bestMatch() != null && responseDto.bestMatch().getWebsite() != null) {
            websiteLink = responseDto.bestMatch().getWebsite();
        }



        String url = "https://graph.facebook.com/v17.0/" + numberID + "/messages";
        String jsonBody = """
    {
      "messaging_product": "whatsapp",
      "to": "%s",
      "type": "template",
      "template": {
        "name": "vacancies",
        "language": {
          "code": "en"
        },
        "components": [
              {
                "type": "body",
                "parameters": [
                  { "type": "text", "text": "Kevin" },
                  { "type": "text", "text": "%s" },
                  { "type": "text", "text": "-" },
                  { "type": "text", "text": "-" },
                  { "type": "text", "text": "-" },
                  { "type": "text", "text": "-" }
                ]
              }
            ]\s
      }
    }
    """.formatted(telefoonNummer, websiteLink);

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


    public String fetchQueryResponseJson() throws IOException, InterruptedException {
        String url = "http://localhost:8080/api/v1/matcher";

        String requestBody = """
    {
      "phoneNumber": "0600000000",
      "messageText": "Looking for internship as hbo student! I study graphic design."
    }
    """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }


}
