package com.rebellworksllm.backend.openai.application;

import com.rebellworksllm.backend.openai.application.dto.EmbeddingRequest;
import com.rebellworksllm.backend.openai.application.dto.EmbeddingResponse;
import com.rebellworksllm.backend.openai.application.exception.OpenAIEmbeddingException;
import com.rebellworksllm.backend.openai.config.OpenAICredentials;
import com.rebellworksllm.backend.openai.domain.EmbeddingResult;
import com.rebellworksllm.backend.openai.domain.OpenAIEmbeddingService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class OpenAIEmbeddingServiceImpl implements OpenAIEmbeddingService {

    private final RestTemplate restTemplate;
    private final OpenAICredentials credentials;

    public OpenAIEmbeddingServiceImpl(@Qualifier("openaiRestTemplate") RestTemplate restTemplate, OpenAICredentials credentials) {
        this.restTemplate = restTemplate;
        this.credentials = credentials;
    }

    @Override
    public EmbeddingResult embedText(String text) {
        EmbeddingRequest request = new EmbeddingRequest(credentials.getEmbeddingModel(), text, "float");
        HttpEntity<EmbeddingRequest> entity = new HttpEntity<>(request);

        try {
            ResponseEntity<EmbeddingResponse> response = restTemplate.postForEntity("/embeddings", entity, EmbeddingResponse.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new OpenAIEmbeddingException("OpenAI API error: " + response.getStatusCode());
            }
            return new EmbeddingResult(response.getBody().data().getFirst().embedding());
        } catch (HttpClientErrorException e) {
            throw new OpenAIEmbeddingException("OpenAI API error: " + e.getStatusCode());
        }
    }
}
