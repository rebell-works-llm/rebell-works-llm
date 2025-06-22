package com.rebellworksllm.backend.modules.openai.application;

import com.rebellworksllm.backend.modules.openai.application.dto.ChatCompletionRequest;
import com.rebellworksllm.backend.modules.openai.application.dto.ChatCompletionResponse;
import com.rebellworksllm.backend.modules.openai.application.dto.ChatMessage;
import com.rebellworksllm.backend.modules.openai.application.exception.OpenAICompletionException;
import com.rebellworksllm.backend.modules.openai.application.exception.OpenAIEmbeddingException;
import com.rebellworksllm.backend.modules.openai.config.OpenAICredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class OpenAICompletionServiceImpl implements OpenAICompletionService {

    private static final Logger logger = LoggerFactory.getLogger(OpenAICompletionServiceImpl.class);

    private final RestTemplate restTemplate;
    private final OpenAICredentials credentials;

    public OpenAICompletionServiceImpl(@Qualifier("openaiRestTemplate") RestTemplate restTemplate, OpenAICredentials credentials) {
        this.restTemplate = restTemplate;
        this.credentials = credentials;
    }

    @Override
    public String complete(Map<String, String> messages) {
        List<ChatMessage> chatMessages = messages.entrySet().stream()
                .map(e -> new ChatMessage(e.getKey(), e.getValue()))
                .toList();

        ChatCompletionRequest request = new ChatCompletionRequest(
                credentials.getCompletionModel(),
                chatMessages,
                0.7
        );

        HttpEntity<ChatCompletionRequest> entity = new HttpEntity<>(request);

        try {
            ResponseEntity<ChatCompletionResponse> response = restTemplate.postForEntity(
                    "/chat/completions",
                    entity,
                    ChatCompletionResponse.class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new OpenAICompletionException("OpenAI API error: " + response.getStatusCode());
            }

            return response.getBody().getFirstMessageContent().trim();
        } catch (HttpClientErrorException e) {
            logger.error("Client error while requesting chat completion from OpenAI: status={}, response={}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new OpenAIEmbeddingException("OpenAI API chat completion error: " + e.getStatusCode(), e);
        } catch (Exception e) {
            logger.error("Unexpected error while requesting chat completion from OpenAI: {}", e.getMessage(), e);
            throw new OpenAIEmbeddingException("Unexpected error from OpenAI chat completion API", e);
        }
    }
}
