package com.rebellworksllm.backend.matching.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "hubspot")
@Validated
public class HubSpotCredentials {

    @NotBlank
    private String apiKey;

    @NotBlank
    private String baseUrl;

    private String contactProperties;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getContactProperties() {
        return contactProperties;
    }

    public void setContactProperties(String contactProperties) {
        this.contactProperties = contactProperties;
    }
}
