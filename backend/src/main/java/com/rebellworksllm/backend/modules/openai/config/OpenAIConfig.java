package com.rebellworksllm.backend.modules.openai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Configuration
public class OpenAIConfig {

    @Bean("openaiRestTemplate")
    public RestTemplate openaiRestTemplate(OpenAICredentials credentials) {
        RestTemplate template = new RestTemplate();
        ClientHttpRequestInterceptor authInterceptor = (request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + credentials.getApiKey());
            request.getHeaders().add("Content-Type", "application/json");
            return execution.execute(request, body);
        };
        template.setInterceptors(List.of(authInterceptor));
        template.setUriTemplateHandler(new org.springframework.web.util.DefaultUriBuilderFactory(credentials.getApiBaseUrl()));
        return template;
    }
}