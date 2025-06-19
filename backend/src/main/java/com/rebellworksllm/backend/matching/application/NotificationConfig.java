package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.whatsapp.domain.WhatsAppService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationConfig {
    @Bean
    public VacancyNotificationAdapter vacancyNotificationAdapter(
            WhatsAppService whatsAppService,
            TemplateService templateService
    ) {
        return new VacancyNotificationAdapter(whatsAppService, templateService);
    }
}
