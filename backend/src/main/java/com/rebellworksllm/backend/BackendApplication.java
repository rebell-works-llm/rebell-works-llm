package com.rebellworksllm.backend;

import com.rebellworksllm.backend.openai.config.OpenAICredentials;
import com.rebellworksllm.backend.hubspot.config.HubSpotCredentials;
import com.rebellworksllm.backend.vacancies.config.PineconeCredentials;
import com.rebellworksllm.backend.vacancies.config.SupabaseCredentials;
import com.rebellworksllm.backend.whatsapp.config.WhatsAppCredentials;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        HubSpotCredentials.class,
        OpenAICredentials.class,
        WhatsAppCredentials.class,
        PineconeCredentials.class,
        SupabaseCredentials.class
})
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}
