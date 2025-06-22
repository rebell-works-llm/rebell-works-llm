package com.rebellworksllm.backend.modules.matching.application;

import com.rebellworksllm.backend.modules.matching.domain.Vacancy;
import com.rebellworksllm.backend.modules.vacancies.application.dto.VacancyResponseDto;
import com.rebellworksllm.backend.modules.whatsapp.domain.WhatsAppService;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public class VacancyNotificationAdapter {

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

    public void sendExtraVacancies(String phoneNumber, Vacancy vac3, Vacancy vac4){

        Vacancy vacancy3 = new Vacancy(vac3.id(), vac3.title(), vac3.description(), vac3.salary(), vac3.workingHours(), vac3.function());
        Vacancy vacancy4 = new Vacancy(vac4.id(), vac4.title(), vac4.description(), vac4.salary(), vac4.workingHours(), vac4.function());


        List<String> parameters = templateTwo.generateVacancyTemplateParams("Student", vacancy3, vacancy4);
        whatsAppService.sendTemplateMessage(phoneNumber, "rebell_template2", "nl", parameters);
    }


}