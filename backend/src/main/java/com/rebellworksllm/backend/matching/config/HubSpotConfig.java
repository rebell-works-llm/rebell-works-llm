package com.rebellworksllm.backend.matching.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class HubSpotConfig {

    private final String apiKey;

    public HubSpotConfig(@Value("${hubspot.api.key}") String apiKey) {
        this.apiKey = apiKey;
    }

    @Bean("HubSpotRestTemplate")
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + apiKey);
            return execution.execute(request, body);
        };

        restTemplate.setInterceptors(List.of(interceptor));
        return restTemplate;
    }
}