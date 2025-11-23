package com.rebellworksllm.backend.common.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "supabase")
@Validated
public class SupabaseCredentials {

    @NotBlank
    private String apiKey;

    @NotBlank
    private String baseUrl;

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
}
