package com.rebellworksllm.backend.hubspot.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "hubspot")
@Validated
public class HubSpotCredentials {

    @NotBlank
    private String apiKey;

    @NotBlank
    private String apiBaseUrl;

    @NotBlank
    private String contactProperties;

    public @NotBlank String getApiKey() {
        return apiKey;
    }

    public void setApiKey(@NotBlank String apiKey) {
        this.apiKey = apiKey;
    }

    public @NotBlank String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public void setApiBaseUrl(@NotBlank String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }

    public String getContactProperties() {
        return contactProperties;
    }

    public void setContactProperties(String contactProperties) {
        this.contactProperties = contactProperties;
    }
}
