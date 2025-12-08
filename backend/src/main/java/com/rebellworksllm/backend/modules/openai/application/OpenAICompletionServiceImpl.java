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
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class OpenAICompletionServiceImpl implements OpenAICompletionService {

    private static final Logger logger = LoggerFactory.getLogger(OpenAICompletionServiceImpl.class);

    private final RestClient restClient;
    private final OpenAICredentials credentials;

    public OpenAICompletionServiceImpl(RestClient restClient, OpenAICredentials credentials) {
        this.restClient = restClient;
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
            ChatCompletionResponse response =
                    restClient.post()
                            .uri("/v1/chat/completions")
                            .body(request)
                            .retrieve()
                            .onStatus(HttpStatusCode::isError,
                                    (req, res) -> {
                                        throw new OpenAICompletionException(
                                                "OpenAI API error: " + res.getStatusCode()
                                        );
                                    }
                            )
                            .body(ChatCompletionResponse.class);

            if (response == null || response.choices().isEmpty()) {
                throw new OpenAICompletionException(
                        "OpenAI API returned empty completion response"
                );
            }

            return response.getFirstMessageContent().trim();
        } catch (OpenAICompletionException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while requesting chat completion from OpenAI", e);
            throw new OpenAICompletionException("Unexpected error from OpenAI chat completion API", e);
        }
    }
}
