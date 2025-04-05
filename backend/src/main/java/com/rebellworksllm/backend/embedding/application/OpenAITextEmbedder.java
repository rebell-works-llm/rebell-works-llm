package com.rebellworksllm.backend.embedding.application;

import com.rebellworksllm.backend.embedding.application.exception.TextEmbeddingException;
import com.rebellworksllm.backend.embedding.domain.TextEmbedder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class OpenAITextEmbedder implements TextEmbedder {

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/embeddings";
    private static final String OPENAI_API_MODEL = "text-embedding-ada-002";

    private final HttpClient httpClient;
    private final String apiKey;

    public OpenAITextEmbedder(HttpClient httpClient, @Value("${openai.api.key}") String apiKey) {
        this.httpClient = httpClient;
        this.apiKey = apiKey;
    }

    @Override
    public float[] embedText(String text) throws TextEmbeddingException {
        try {
            HttpResponse<String> response = httpClient.send(buildRequest(text), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new TextEmbeddingException("OpenAI API returned status " + response.statusCode() + ": " + response.body());
            }

            return parseEmbedding(response.body());
        } catch (IOException | InterruptedException e) {
            throw new TextEmbeddingException("Failed to send request to OpenAI: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private HttpRequest buildRequest(String text) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", OPENAI_API_MODEL);
        requestBody.put("input", text);
        requestBody.put("encoding_format", "float");

        return HttpRequest.newBuilder()
                .uri(URI.create(OPENAI_API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toJSONString()))
                .build();
    }

    private float[] parseEmbedding(String responseBody) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonResponse = (JSONObject) parser.parse(responseBody);
            JSONArray dataArray = (JSONArray) jsonResponse.get("data");
            JSONObject firstData = (JSONObject) dataArray.getFirst();
            JSONArray embeddingArray = (JSONArray) firstData.get("embedding");

            float[] embeddings = new float[embeddingArray.size()];
            for (int i = 0; i < embeddingArray.size(); i++) {
                embeddings[i] = Float.parseFloat(embeddingArray.get(i).toString());
            }

            return embeddings;
        } catch (ParseException e) {
            throw new TextEmbeddingException("Failed to parse embedding: " + e.getMessage());
        }
    }
}
