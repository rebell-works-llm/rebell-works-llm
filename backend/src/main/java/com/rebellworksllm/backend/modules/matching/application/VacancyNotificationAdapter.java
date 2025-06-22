package com.rebellworksllm.backend.modules.matching.application;

import com.rebellworksllm.backend.modules.matching.domain.Vacancy;
import com.rebellworksllm.backend.modules.vacancies.application.dto.VacancyResponseDto;
import com.rebellworksllm.backend.modules.whatsapp.domain.WhatsAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public class VacancyNotificationAdapter {

    private static final Logger logger = LoggerFactory.getLogger(HubSpotWebhookService.class);
    private final WhatsAppService whatsAppService;
    private final TemplateService templateOne;
    private final TemplateService templateTwo;

    public VacancyNotificationAdapter(WhatsAppService whatsAppService,
                                      @Qualifier("templateOne") TemplateService templateOne,
                                      @Qualifier("templateTwo") TemplateService templateTwo) {
        this.whatsAppService = whatsAppService;
        this.templateOne = templateOne;
        this.templateTwo = templateTwo;
    }

    public void notifyCandidate(String phoneNumber, String name, Vacancy vac1, Vacancy vac2) {
        List<String> parameters = templateOne.generateVacancyTemplateParams(name, vac1, vac2);
        whatsAppService.sendTemplateMessage(phoneNumber, "rebell_template", "nl", parameters);
    }

    public void sendExtraVacancies(String phoneNumber, VacancyResponseDto vac3, VacancyResponseDto vac4){

        Vacancy vacancy3 = new Vacancy(vac3.id(), vac3.title(), vac3.description(), vac3.salary(), vac3.workingHours(), vac3.function());
        Vacancy vacancy4 = new Vacancy(vac4.id(), vac4.title(), vac4.description(), vac4.salary(), vac4.workingHours(), vac4.function());

        logger.info("poep {}", vac4);
        logger.info("vacancy 4 {}", vacancy4);

        List<String> parameters = templateTwo.generateVacancyTemplateParams("Student", vacancy3, vacancy4);
        whatsAppService.sendTemplateMessage(phoneNumber, "rebell_template2", "nl", parameters);
    }

    public void notifyInterestedCanidate(String phoneNumber){
        whatsAppService.sendTemplateMessage(phoneNumber, "rebell_template3", "nl", null);
    }


}