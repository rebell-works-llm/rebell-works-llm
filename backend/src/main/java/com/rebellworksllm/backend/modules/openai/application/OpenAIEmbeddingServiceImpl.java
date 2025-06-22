package com.rebellworksllm.backend.modules.openai.application;

import com.rebellworksllm.backend.modules.openai.application.dto.EmbeddingRequest;
import com.rebellworksllm.backend.modules.openai.application.dto.EmbeddingResponse;
import com.rebellworksllm.backend.modules.openai.application.exception.OpenAIEmbeddingException;
import com.rebellworksllm.backend.modules.openai.config.OpenAICredentials;
import com.rebellworksllm.backend.modules.openai.application.dto.EmbeddingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class OpenAIEmbeddingServiceImpl implements OpenAIEmbeddingService {

    private static final Logger logger = LoggerFactory.getLogger(OpenAIEmbeddingServiceImpl.class);

    private final RestTemplate restTemplate;
    private final OpenAICredentials credentials;

    public OpenAIEmbeddingServiceImpl(@Qualifier("openaiRestTemplate") RestTemplate restTemplate, OpenAICredentials credentials) {
        this.restTemplate = restTemplate;
        this.credentials = credentials;
    }

    @Override
    public EmbeddingResult embedText(String text) {
        logger.info("Requesting embedding from OpenAI for text with length: {} (model: {})",
                text != null ? text.length() : 0,
                credentials.getEmbeddingModel());

        try {
            EmbeddingRequest request = new EmbeddingRequest(credentials.getEmbeddingModel(), text, "float");
            HttpEntity<EmbeddingRequest> entity = new HttpEntity<>(request);

            ResponseEntity<EmbeddingResponse> response = restTemplate.postForEntity("/embeddings", entity, EmbeddingResponse.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                logger.error("Received non-2xx status or empty body from OpenAI: status={}, body={}",
                        response.getStatusCode(), response.getBody());
                throw new OpenAIEmbeddingException("OpenAI API error: " + response.getStatusCode());
            }

            logger.info("Successfully received embedding for text with length: {}",
                    text != null ? text.length() : 0);

            return new EmbeddingResult(response.getBody().data().getFirst().embedding());
        } catch (HttpClientErrorException e) {
            logger.error("Client error while requesting embedding from OpenAI: status={}, response={}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new OpenAIEmbeddingException("OpenAI API error: " + e.getStatusCode(), e);
        } catch (Exception e) {
            logger.error("Unexpected error while requesting embedding from OpenAI: {}", e.getMessage(), e);
            throw new OpenAIEmbeddingException("Unexpected error from OpenAI embedding API", e);
        }
    }
}
