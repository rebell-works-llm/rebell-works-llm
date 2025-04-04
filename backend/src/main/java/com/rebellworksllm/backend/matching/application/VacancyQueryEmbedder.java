package com.rebellworksllm.backend.matching.application;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class VacancyQueryEmbedder {

    public float[] embedQueryToVector(String query){
        String apiKey = System.getenv("OPENAI_API_KEY");
        String url = "https://api.openai.com/v1/embeddings";

        JSONObject requestBody = new JSONObject()
                .put("model", "text-embedding-ada-002")
                .put("input", query)
                .put("encoding_format", "float");

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject jsonResponse = new JSONObject(response.body());

            var embeddingArray = jsonResponse.getJSONArray("data")
                    .getJSONObject(0)
                    .getJSONArray("embedding");

            float[] embeddings = new float[embeddingArray.length()];
            for (int i = 0; i < embeddingArray.length(); i++) {
                embeddings[i] = embeddingArray.getFloat(i);
            }

            return embeddings;
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
