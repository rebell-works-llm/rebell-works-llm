package com.rebellworksllm.backend.vacancies.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.List;

@Configuration
public class PineconeConfig {

    @Bean("pineconeRestTemplate")
    public RestTemplate pineconeRestTemplate(PineconeCredentials credentials) {
        RestTemplate template = new RestTemplate();
        ClientHttpRequestInterceptor authInterceptor = (request, body, execution) -> {
            request.getHeaders().add("Api-Key", credentials.getApiKey());
            request.getHeaders().add("Content-Type", "application/json");
            return execution.execute(request, body);
        };
        template.setInterceptors(List.of(authInterceptor));
        template.setUriTemplateHandler(new DefaultUriBuilderFactory(credentials.getBaseUrl()));
        return template;
    }
}
