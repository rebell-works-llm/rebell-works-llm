package com.rebellworksllm.backend.modules.hubspot.config;

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

    @NotBlank
    private String clientSecret;

    @NotBlank
    private String testObject;

    @NotBlank
    private String testPhone;

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

    public @NotBlank String getClientSecret() {
        return clientSecret;
    }


    public void setClientSecret(@NotBlank String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getTestObject() {
        return testObject;
    }

    public void setTestObject(String testObject) {
        this.testObject = testObject;
    }

    public String getTestPhone() {
        return testPhone;
    }

    public void setTestPhone(String testPhone) {
        this.testPhone = testPhone;
    }
}
