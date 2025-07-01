package com.rebellworksllm.backend.config.supabase;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SupabaseConfig {

    @Bean
    public RestTemplate supabaseRestTemplate(RestTemplateBuilder builder,
                                             SupabaseCredentials credentials) {
        return builder
                .rootUri(credentials.getBaseUrl())
                .interceptors((request, body, execution) -> {
                    request.getHeaders().add("apikey", credentials.getApiKey());
                    request.getHeaders().add("Authorization", "Bearer " + credentials.getApiKey());
                    return execution.execute(request, body);
                })
                .build();
    }
}
