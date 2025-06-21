package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.email.application.EmailService;
import com.rebellworksllm.backend.hubspot.application.HubSpotStudentProvider;
import com.rebellworksllm.backend.hubspot.application.dto.StudentContact;
import com.rebellworksllm.backend.matching.application.util.MatchingUtils;
import com.rebellworksllm.backend.matching.data.MatchMessageRepository;
import com.rebellworksllm.backend.matching.data.dto.MatchMessageResponse;
import com.rebellworksllm.backend.vacancies.application.VacancyProvider;
import com.rebellworksllm.backend.vacancies.application.dto.VacancyResponseDto;
import com.rebellworksllm.backend.whatsapp.application.dto.ContactResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StudentInterestHandlerServiceImpl implements StudentInterestHandlerService {

    private static final Logger logger = LoggerFactory.getLogger(StudentInterestHandlerServiceImpl.class);

    private final HubSpotStudentProvider studentProvider;
    private final EmailService emailService;
    private final MatchMessageRepository matchMessageRepository;
    private final VacancyProvider vacancyProvider;

    private final String mailTo;

    public StudentInterestHandlerServiceImpl(HubSpotStudentProvider studentProvider,
                                             EmailService emailService, MatchMessageRepository matchMessageRepository, VacancyProvider vacancyProvider,
                                             @Value("${mail.to}") String mailTo
    ) {
        this.studentProvider = studentProvider;
        this.emailService = emailService;
        this.matchMessageRepository = matchMessageRepository;
        this.vacancyProvider = vacancyProvider;
        this.mailTo = mailTo;
    }

    @Override
    public void handleReply(ContactResponseMessage responseMessage) {
        StudentContact studentContact = studentProvider.getStudentByPhone(responseMessage.contactPhone());
        logger.info("Interested student found: {}", studentContact.fullName());

        String normalizedPhone = MatchingUtils.normalizePhone(responseMessage.contactPhone());
        MatchMessageResponse matchMessageResponse = matchMessageRepository.findByContactPhone(normalizedPhone);
        logger.info("Match message found: {}", matchMessageResponse);

        String vacancyId = matchMessageResponse.vacancyIds().get(0);
        if (responseMessage.message().endsWith("2")) {
            vacancyId = matchMessageResponse.vacancyIds().get(1);
        }

        VacancyResponseDto vacancy = vacancyProvider.getVacancyById(vacancyId);
        System.out.println(vacancy);

        String htmlBody = String.format("""
                        <html>
                        <body style="font-family: Arial, sans-serif; color: #333;">
                            <h2 style="color: #2c3e50;">📩 New Student Interest</h2>

                            <p><strong>HubSpot ID:</strong> %s</p>
                            <p><strong>Name:</strong> %s</p>
                            <p><strong>Phone:</strong> %s</p>

                            <p><strong>Vacancy id:</strong><br/>"%s"</p>
                            <p><strong>Vacancy title:</strong><br/>"%s"</p>
                            <p><strong>Vacancy website:</strong><br/>"%s"</p>

                            <p><strong>Actie:</strong><br/>"%s"</p>
                        </body>
                        </html>
                        """,
                studentContact.id(),
                studentContact.fullName(),
                studentContact.phoneNumber(),
                vacancy.id(),
                vacancy.title(),
                vacancy.link(),
                responseMessage.message()
        );

        emailService.sendWithHtml(mailTo, "New Student Interest", htmlBody);
        logger.info("Email successfully sent to: {}", studentContact.fullName());

    }
}
