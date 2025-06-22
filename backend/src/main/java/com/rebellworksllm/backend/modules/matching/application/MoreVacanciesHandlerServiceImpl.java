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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("extraVacancyHandler")
public class MoreVacanciesHandlerServiceImpl implements StudentInterestHandlerService {

    private static final Logger logger = LoggerFactory.getLogger(HubSpotWebhookService.class);
    private final HubSpotStudentProvider studentProvider;
    private final MatchMessageRepository matchMessageRepository;
    private final VacancyProvider vacancyProvider;

    private final VacancyNotificationAdapter vacancyNotificationAdapter;

    public MoreVacanciesHandlerServiceImpl(HubSpotStudentProvider studentProvider,
                                           MatchMessageRepository matchMessageRepository,
                                           VacancyProvider vacancyProvider,
                                           VacancyNotificationAdapter vacancyNotificationAdapter) {
        this.studentProvider = studentProvider;
        this.matchMessageRepository = matchMessageRepository;
        this.vacancyProvider = vacancyProvider;
        this.vacancyNotificationAdapter = vacancyNotificationAdapter;
    }

    @Override
    public void handleReply(ContactResponseMessage responseMessage) {

        StudentContact studentContact = studentProvider.getStudentByPhone(responseMessage.contactPhone());
        String normalizedPhone = MatchingUtils.normalizePhone(responseMessage.contactPhone());
        MatchMessageResponse matchMessageResponse = matchMessageRepository.findByContactPhone(normalizedPhone);

        String vacancy3Id = matchMessageResponse.vacancyIds().get(2);
        String vacancy4Id = matchMessageResponse.vacancyIds().get(3);



        VacancyResponseDto vac3 = vacancyProvider.getVacancyById(vacancy3Id);
        VacancyResponseDto vac4 = vacancyProvider.getVacancyById(vacancy4Id);

        logger.info("poep1 {}", vac4);
        logger.info("VacatureDTO4 {}", vac4 );


        vacancyNotificationAdapter.sendExtraVacancies(studentContact.phoneNumber(), vac3, vac4);

    }
}
