package com.rebellworksllm.backend.modules.matching.application;

import com.rebellworksllm.backend.modules.hubspot.application.HubSpotStudentProvider;
import com.rebellworksllm.backend.modules.hubspot.application.dto.StudentContact;
import com.rebellworksllm.backend.modules.matching.application.util.MatchingUtils;
import com.rebellworksllm.backend.modules.matching.data.MatchMessageRepository;
import com.rebellworksllm.backend.modules.matching.data.dto.MatchMessageResponse;
import com.rebellworksllm.backend.modules.matching.domain.Vacancy;
import com.rebellworksllm.backend.modules.vacancies.application.VacancyProvider;
import com.rebellworksllm.backend.modules.vacancies.application.dto.VacancyResponseDto;
import com.rebellworksllm.backend.modules.whatsapp.application.dto.ContactResponseMessage;
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
    private final TemplateService templateThree;
    private final MatchMessageRepository matchMessageRepository;
    private final VacancyProvider vacancyProvider;

    public VacancyNotificationAdapter(WhatsAppService whatsAppService,
                                      @Qualifier("templateOne") TemplateService templateOne,
                                      @Qualifier("templateTwo") TemplateService templateTwo,
                                      @Qualifier("templateThree") TemplateService templateThree,
                                      MatchMessageRepository matchMessageRepository,
                                      VacancyProvider vacancyProvider) {
        this.whatsAppService = whatsAppService;
        this.templateOne = templateOne;
        this.templateTwo = templateTwo;
        this.templateThree = templateThree;
        this.matchMessageRepository = matchMessageRepository;
        this.vacancyProvider = vacancyProvider;
    }

    public void notifyCandidate(String phoneNumber, String name, Vacancy vac1, Vacancy vac2) {
        List<String> parameters = templateOne.generateVacancyTemplateParams(name, vac1, vac2, null, null, null);
        whatsAppService.sendTemplateMessage(phoneNumber, "rebell_template", "nl", parameters);
    }

    public void sendExtraVacancies(String phoneNumber, VacancyResponseDto vac3, VacancyResponseDto vac4){

        Vacancy vacancy3 = new Vacancy(vac3.id(), vac3.title(), vac3.description(), vac3.salary(), vac3.workingHours(), vac3.function());
        Vacancy vacancy4 = new Vacancy(vac4.id(), vac4.title(), vac4.description(), vac4.salary(), vac4.workingHours(), vac4.function());

        logger.info("poep {}", vac4);
        logger.info("vacancy 4 {}", vacancy4);

        List<String> parameters = templateTwo.generateVacancyTemplateParams("Student", vacancy3, vacancy4, null, null, null);
        whatsAppService.sendTemplateMessage(phoneNumber, "rebell_template2", "nl", parameters);
    }

    public void notifyInterestedCandidate(String phoneNumber, ContactResponseMessage responseMessage){

        String normalizedPhone = MatchingUtils.normalizePhone(responseMessage.contactPhone());
        MatchMessageResponse matchMessageResponse = matchMessageRepository.findByContactPhone(normalizedPhone);

        String vacancy1Id = matchMessageResponse.vacancyIds().get(2);
        String vacancy2Id = matchMessageResponse.vacancyIds().get(3);
        String vacancy3Id = matchMessageResponse.vacancyIds().get(2);
        String vacancy4Id = matchMessageResponse.vacancyIds().get(3);

        VacancyResponseDto vac1 = vacancyProvider.getVacancyById(vacancy1Id);
        VacancyResponseDto vac2 = vacancyProvider.getVacancyById(vacancy2Id);
        VacancyResponseDto vac3 = vacancyProvider.getVacancyById(vacancy3Id);
        VacancyResponseDto vac4 = vacancyProvider.getVacancyById(vacancy4Id);

        Vacancy vacancy1 = new Vacancy(vac1.id(), vac1.title(), vac1.description(), vac1.salary(), vac1.workingHours(), vac1.function());
        Vacancy vacancy2 = new Vacancy(vac2.id(), vac2.title(), vac2.description(), vac2.salary(), vac2.workingHours(), vac4.function());
        Vacancy vacancy3 = new Vacancy(vac3.id(), vac3.title(), vac3.description(), vac3.salary(), vac3.workingHours(), vac3.function());
        Vacancy vacancy4 = new Vacancy(vac4.id(), vac4.title(), vac4.description(), vac4.salary(), vac4.workingHours(), vac4.function());

        List<String> parameters = templateThree.generateVacancyTemplateParams("Student", vacancy1, vacancy2, vacancy3, vacancy4, responseMessage);
        whatsAppService.sendTemplateMessage(phoneNumber, "rebell_template5", "nl", parameters);
    }


}