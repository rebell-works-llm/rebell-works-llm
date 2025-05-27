package com.rebellworksllm.backend.hubspot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class HubSpotConfig {

    @Bean("hubspotRestTemplate")
    public RestTemplate hubspotRestTemplate(HubSpotCredentials credentials) {
        RestTemplate template = new RestTemplate();
        ClientHttpRequestInterceptor authInterceptor = (request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + credentials.getApiKey());
            return execution.execute(request, body);
        };
        template.setInterceptors(List.of(authInterceptor));
        return template;
    }
}