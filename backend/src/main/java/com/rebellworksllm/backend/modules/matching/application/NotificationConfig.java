package com.rebellworksllm.backend.modules.matching.application;

import com.rebellworksllm.backend.modules.matching.data.MatchMessageRepository;
import com.rebellworksllm.backend.modules.vacancies.application.VacancyProvider;
import com.rebellworksllm.backend.modules.whatsapp.domain.WhatsAppService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationConfig {
    @Bean
    public VacancyNotificationAdapter vacancyNotificationAdapter(
            WhatsAppService whatsAppService,
            @Qualifier("templateOne") TemplateService firstTemplateService,
            @Qualifier("templateTwo") TemplateService secondTemplateService,
            @Qualifier("templateThree") TemplateService thirdTemplate,
            MatchMessageRepository matchMessageRepository,
            VacancyProvider vacancyProvider



    ) {
        return new VacancyNotificationAdapter(whatsAppService, firstTemplateService, secondTemplateService, thirdTemplate, matchMessageRepository, vacancyProvider);
    }
}
