package com.rebellworksllm.backend.modules.matching.application;

import com.rebellworksllm.backend.modules.matching.domain.Vacancy;
import com.rebellworksllm.backend.modules.whatsapp.domain.WhatsAppService;

import java.util.List;

public class VacancyNotificationAdapter {

    private final WhatsAppService whatsAppService;
    private final TemplateService templateService;

    public VacancyNotificationAdapter(WhatsAppService whatsAppService,
                                      TemplateService templateService) {
        this.whatsAppService = whatsAppService;
        this.templateService = templateService;
    }

    public void notifyCandidate(String phoneNumber, String name, Vacancy vac1, Vacancy vac2) {
        List<String> parameters = templateService.generateVacancyTemplateParams(name, vac1, vac2);
        whatsAppService.sendTemplateMessage(phoneNumber, "rebell_template", "nl", parameters);
    }

}