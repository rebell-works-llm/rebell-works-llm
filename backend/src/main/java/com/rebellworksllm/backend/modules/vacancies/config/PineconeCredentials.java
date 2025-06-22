package com.rebellworksllm.backend.modules.vacancies.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "pinecone")
@Validated
public class PineconeCredentials {

    @NotBlank
    private String apiKey;

    @NotBlank
    private String baseUrl;

    @NotBlank
    private String indexName;

    public @NotBlank String getApiKey() {
        return apiKey;
    }

    public void setApiKey(@NotBlank String apiKey) {
        this.apiKey = apiKey;
    }

    public @NotBlank String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(@NotBlank String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public @NotBlank String getIndexName() {
        return indexName;
    }

    public void setIndexName(@NotBlank String indexName) {
        this.indexName = indexName;
    }
}
