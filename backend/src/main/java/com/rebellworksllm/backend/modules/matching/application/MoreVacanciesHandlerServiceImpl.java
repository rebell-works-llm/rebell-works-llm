package com.rebellworksllm.backend.modules.matching.application;

import com.rebellworksllm.backend.modules.hubspot.application.HubSpotStudentProvider;
import com.rebellworksllm.backend.modules.hubspot.application.dto.StudentContact;
import com.rebellworksllm.backend.modules.matching.application.util.MatchingUtils;
import com.rebellworksllm.backend.modules.matching.data.MatchMessageRepository;
import com.rebellworksllm.backend.modules.matching.data.dto.MatchMessageResponse;
import com.rebellworksllm.backend.modules.vacancies.application.VacancyProvider;
import com.rebellworksllm.backend.modules.vacancies.application.dto.VacancyResponseDto;
import com.rebellworksllm.backend.modules.whatsapp.application.dto.ContactResponseMessage;
import org.springframework.stereotype.Service;

@Service("extraVacancyHandler")
public class MoreVacanciesHandlerServiceImpl implements StudentInterestHandlerService {

    private final HubSpotStudentProvider studentProvider;
    private final MatchMessageRepository matchMessageRepository;
    private final VacancyProvider vacancyProvider;

    public MoreVacanciesHandlerServiceImpl(HubSpotStudentProvider studentProvider, MatchMessageRepository matchMessageRepository, VacancyProvider vacancyProvider) {
        this.studentProvider = studentProvider;
        this.matchMessageRepository = matchMessageRepository;
        this.vacancyProvider = vacancyProvider;
    }

    @Override
    public void handleReply(ContactResponseMessage responseMessage) {

        StudentContact studentContact = studentProvider.getStudentByPhone(responseMessage.contactPhone());
        String normalizedPhone = MatchingUtils.normalizePhone(responseMessage.contactPhone());
        MatchMessageResponse matchMessageResponse = matchMessageRepository.findByContactPhone(normalizedPhone);

        String vacancy3Id = matchMessageResponse.vacancyIds().get(2);
        String vacancy4Id = matchMessageResponse.vacancyIds().get(3);

        VacancyResponseDto vacancy3 = vacancyProvider.getVacancyById(vacancy3Id);
        VacancyResponseDto vacancy4 = vacancyProvider.getVacancyById(vacancy4Id);



    }
}
