package com.rebellworksllm.backend.modules.matching.application;

import com.rebellworksllm.backend.common.utils.LogUtils;
import com.rebellworksllm.backend.modules.email.application.EmailService;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("mailHandler")
public class StudentInterestHandlerServiceImpl implements StudentInterestHandlerService {

    private static final Logger logger = LoggerFactory.getLogger(StudentInterestHandlerServiceImpl.class);

    private final HubSpotStudentProvider studentProvider;
    private final EmailService emailService;
    private final MatchMessageRepository matchMessageRepository;
    private final VacancyProvider vacancyProvider;

    @Value("${mail.to.student-interest}")
    private String mailTo;

    public StudentInterestHandlerServiceImpl(HubSpotStudentProvider studentProvider,
                                             EmailService emailService,
                                             MatchMessageRepository matchMessageRepository,
                                             VacancyProvider vacancyProvider
    ) {
        this.studentProvider = studentProvider;
        this.emailService = emailService;
        this.matchMessageRepository = matchMessageRepository;
        this.vacancyProvider = vacancyProvider;
    }

    @Override
    public void handleReply(ContactResponseMessage responseMessage) {
        StudentContact studentContact = studentProvider.getStudentByPhone(responseMessage.contactPhone());
        logger.info("Interested student found: {}", studentContact.fullName());

        String normalizedPhone = MatchingUtils.normalizePhone(responseMessage.contactPhone());
        MatchMessageResponse matchMessageResponse = matchMessageRepository.findByContactPhone(normalizedPhone);
        logger.info("Match message found for student: {}", studentContact.fullName());

        String vacancyId = matchMessageResponse.vacancyIds().get(0);
        if (responseMessage.message().endsWith("2")) {
            vacancyId = matchMessageResponse.vacancyIds().get(1);
        }

        VacancyResponseDto vacancy = vacancyProvider.getVacancyById(vacancyId);
        System.out.println(vacancy);

        String plainBody = """
        New Student Interest
        
        Action: %s

        HubSpot ID: %s
        Name: %s
        Phone: %s
        
        Vacancy ID: %s
        Vacancy title: %s
        Vacancy website: %s
        """.formatted(
                responseMessage.message(),
                studentContact.id(),
                studentContact.fullName(),
                studentContact.phoneNumber(),
                vacancy.id(),
                vacancy.title(),
                vacancy.link()
        );
        emailService.send(mailTo, "New Student Interest", plainBody);
        logger.info("Email successfully sent to: {}", LogUtils.maskEmail(mailTo));
    }
}
