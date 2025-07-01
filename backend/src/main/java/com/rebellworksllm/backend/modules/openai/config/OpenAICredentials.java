package com.rebellworksllm.backend.modules.openai.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "openai")
@Validated
public class OpenAICredentials {

    @NotBlank
    private String apiKey;

    @NotBlank
    private String apiBaseUrl;

    @NotBlank
    private String embeddingModel;

    @NotBlank
    private String completionModel;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public @NotBlank String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public void setApiBaseUrl(@NotBlank String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }

    public @NotBlank String getEmbeddingModel() {
        return embeddingModel;
    }

    public void setEmbeddingModel(@NotBlank String embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public @NotBlank String getCompletionModel() {
        return completionModel;
    }

    public void setCompletionModel(@NotBlank String completionModel) {
        this.completionModel = completionModel;
    }
}
