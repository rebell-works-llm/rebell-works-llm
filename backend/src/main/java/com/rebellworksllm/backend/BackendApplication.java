package com.rebellworksllm.backend;

import com.rebellworksllm.backend.modules.openai.config.OpenAICredentials;
import com.rebellworksllm.backend.modules.hubspot.config.HubSpotCredentials;
import com.rebellworksllm.backend.modules.vacancies.config.PineconeCredentials;
import com.rebellworksllm.backend.config.supabase.SupabaseCredentials;
import com.rebellworksllm.backend.modules.whatsapp.config.WhatsAppCredentials;
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
