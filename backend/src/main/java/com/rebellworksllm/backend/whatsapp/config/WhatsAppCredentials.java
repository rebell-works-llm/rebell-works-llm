package com.rebellworksllm.backend.whatsapp.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "whatsapp")
@Validated
public class WhatsAppCredentials {

    @NotBlank
    private String apiKey;

    @NotBlank
    private String apiBaseUrl;

    @NotBlank
    private String phoneNumberId;

    @NotBlank
    private String verifyToken;

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

    public @NotBlank String getPhoneNumberId() {
        return phoneNumberId;
    }

    public void setPhoneNumberId(@NotBlank String phoneNumberId) {
        this.phoneNumberId = phoneNumberId;
    }

    public @NotBlank String getVerifyToken() {
        return verifyToken;
    }

    public void setVerifyToken(@NotBlank String verifyToken) {
        this.verifyToken = verifyToken;
    }
}