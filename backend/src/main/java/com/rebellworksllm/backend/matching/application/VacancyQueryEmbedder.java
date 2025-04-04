package com.rebellworksllm.backend.matching.application;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@SuppressWarnings("unchecked")
@Service
public class VacancyQueryEmbedder {

    public float[] embedQueryToVector(String query) {
        String apiKey = System.getenv("OPENAI_API_KEY");
        String url = "https://api.openai.com/v1/embeddings";

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "text-embedding-ada-002");
        requestBody.put("input", query);
        requestBody.put("encoding_format", "float");

        try(HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toJSONString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse response using Simple JSON
            JSONParser parser = new JSONParser();
            JSONObject jsonResponse = (JSONObject) parser.parse(response.body());
            JSONArray dataArray = (JSONArray) jsonResponse.get("data");
            JSONObject firstData = (JSONObject) dataArray.getFirst();
            JSONArray embeddingArray = (JSONArray) firstData.get("embedding");

            float[] embeddings = new float[embeddingArray.size()];
            for (int i = 0; i < embeddingArray.size(); i++) {
                embeddings[i] = ((Number) embeddingArray.get(i)).floatValue();
            }

            return embeddings;
        } catch (IOException | InterruptedException | ParseException e) {
            throw new RuntimeException("Failed to fetch or parse embedding response", e);
        }
    }
}
