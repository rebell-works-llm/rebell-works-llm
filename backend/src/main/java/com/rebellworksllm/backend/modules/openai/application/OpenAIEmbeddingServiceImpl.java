package com.rebellworksllm.backend.modules.openai.application;

import com.rebellworksllm.backend.modules.openai.application.dto.EmbeddingRequest;
import com.rebellworksllm.backend.modules.openai.application.dto.EmbeddingResponse;
import com.rebellworksllm.backend.modules.openai.application.exception.OpenAIEmbeddingException;
import com.rebellworksllm.backend.modules.openai.config.OpenAICredentials;
import com.rebellworksllm.backend.modules.openai.application.dto.EmbeddingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class OpenAIEmbeddingServiceImpl implements OpenAIEmbeddingService {

    private static final Logger logger = LoggerFactory.getLogger(OpenAIEmbeddingServiceImpl.class);

    private final RestClient restClient;
    private final OpenAICredentials credentials;

    public OpenAIEmbeddingServiceImpl(
            RestClient openaiRestClient,
            OpenAICredentials credentials
    ) {
        this.restClient = openaiRestClient;
        this.credentials = credentials;
    }

    @Override
    public EmbeddingResult embedText(String text) {
        logger.info("Requesting embedding from OpenAI for text with length: {} (model: {})",
                text != null ? text.length() : 0,
                credentials.getEmbeddingModel());

        EmbeddingRequest request = new EmbeddingRequest(credentials.getEmbeddingModel(), text, "float");

        try {
            EmbeddingResponse response =
                    restClient.post()
                            .uri("/v1/embeddings")
                            .body(request)
                            .retrieve()
                            .onStatus(
                                    HttpStatusCode::isError,
                                    (req, res) -> {
                                        throw new OpenAIEmbeddingException(
                                                "OpenAI API error: " + res.getStatusCode()
                                        );
                                    }
                            )
                            .body(EmbeddingResponse.class);

            if (response == null || response.data().isEmpty()) {
                throw new OpenAIEmbeddingException(
                        "OpenAI API returned empty embedding response"
                );
            }

            logger.info("Successfully received embedding for text with length: {}", text != null ? text.length() : 0);

            return new EmbeddingResult(response.data().getFirst().embedding());
        } catch (OpenAIEmbeddingException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while requesting embedding from OpenAI", e);
            throw new OpenAIEmbeddingException("Unexpected error from OpenAI embedding API", e);
        }
    }
}