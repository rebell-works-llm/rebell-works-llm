package com.rebellworksllm.backend.modules.matching.application;

import com.rebellworksllm.backend.common.utils.ErrorNotificationService;
import com.rebellworksllm.backend.common.utils.LogUtils;
import com.rebellworksllm.backend.modules.hubspot.application.HubSpotStudentProvider;
import com.rebellworksllm.backend.modules.hubspot.application.dto.StudentContact;
import com.rebellworksllm.backend.modules.matching.application.util.MatchingUtils;
import com.rebellworksllm.backend.modules.matching.data.MatchMessageRepository;
import com.rebellworksllm.backend.modules.matching.data.dto.MatchMessageResponse;
import com.rebellworksllm.backend.modules.vacancies.application.VacancyProvider;
import com.rebellworksllm.backend.modules.vacancies.application.dto.VacancyResponseDto;
import com.rebellworksllm.backend.modules.whatsapp.application.dto.ContactResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("extraVacancyHandler")
public class MoreVacanciesHandlerServiceImpl implements StudentInterestHandlerService {

    private static final Logger logger = LoggerFactory.getLogger(MoreVacanciesHandlerServiceImpl.class);
    private final HubSpotStudentProvider studentProvider;
    private final MatchMessageRepository matchMessageRepository;
    private final VacancyProvider vacancyProvider;
    private final VacancyNotificationAdapter vacancyNotificationAdapter;
    private final ErrorNotificationService errorNotificationService;

    public MoreVacanciesHandlerServiceImpl(
            HubSpotStudentProvider studentProvider,
            MatchMessageRepository matchMessageRepository,
            VacancyProvider vacancyProvider,
            VacancyNotificationAdapter vacancyNotificationAdapter,
            ErrorNotificationService errorNotificationService
    ) {
        this.studentProvider = studentProvider;
        this.matchMessageRepository = matchMessageRepository;
        this.vacancyProvider = vacancyProvider;
        this.vacancyNotificationAdapter = vacancyNotificationAdapter;
        this.errorNotificationService = errorNotificationService;
    }

    @Override
    public void handleReply(ContactResponseMessage responseMessage) {
        try {
            handleMoreVacancies(responseMessage);
        } catch (Exception e) {
            handleError(e, responseMessage);
        }
    }

    private void handleMoreVacancies(ContactResponseMessage responseMessage) {
        StudentContact studentContact = studentProvider.getStudentByPhone(responseMessage.contactPhone());
        String normalizedPhone = MatchingUtils.normalizePhone(responseMessage.contactPhone());
        MatchMessageResponse matchMessageResponse = matchMessageRepository.findByContactPhone(normalizedPhone);

        String vacancy3Id = matchMessageResponse.vacancyIds().get(2);
        VacancyResponseDto vac3 = vacancyProvider.getVacancyById(vacancy3Id);

        String vacancy4Id = matchMessageResponse.vacancyIds().get(3);
        VacancyResponseDto vac4 = vacancyProvider.getVacancyById(vacancy4Id);

        vacancyNotificationAdapter.sendExtraVacancies(studentContact.phoneNumber(), vac3, vac4);
    }

    private void handleError(Exception e, ContactResponseMessage responseMessage) {
        logger.error("Error while handling 'Meer vacatures' reply", e);

        String contextInfo = String.format("""
                        Incoming message:
                        - contactPhone: %s
                        - message: %s
                        """,
                responseMessage != null ? LogUtils.maskPhone(responseMessage.contactPhone()) : "<null>",
                responseMessage != null ? responseMessage.message() : "<null>"
        );
        errorNotificationService.sendErrorEmail("Extra Vacancies Handling", contextInfo, e);
        logger.warn("Extra vacancies handling aborted due to error; webhook processing continues.");
    }
}
