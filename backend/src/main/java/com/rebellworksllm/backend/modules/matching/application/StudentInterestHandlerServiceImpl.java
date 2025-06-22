package com.rebellworksllm.backend.modules.matching.application;

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
                          <body style="background: #f8f9fa; font-family: Arial, sans-serif; color: #333;">
                            <div style="max-width: 520px; margin: 40px auto; background: #fff; border-radius: 16px; box-shadow: 0 4px 24px rgba(44, 62, 80, .07); padding: 32px;">
                              <h2 style="color: #3476de; margin-bottom: 24px; font-weight: 700;">
                                <span style="font-size: 1.6em;">📩</span> New Student Interest
                              </h2>
                              <div style="margin-bottom: 16px;">
                                <span style="color: #888; font-size: 0.95em;">HubSpot ID:</span>
                                <span style="color: #222; font-weight: bold;">%s</span>
                              </div>
                              <div style="margin-bottom: 16px;">
                                <span style="color: #888; font-size: 0.95em;">Name:</span>
                                <span style="color: #222; font-weight: bold;">%s</span>
                              </div>
                              <div style="margin-bottom: 24px;">
                                <span style="color: #888; font-size: 0.95em;">Phone:</span>
                                <span style="color: #2c3e50;">%s</span>
                              </div>
                              <hr style="margin: 24px 0; border: none; border-top: 1px solid #eee;">
                              <div style="margin-bottom: 16px;">
                                <span style="color: #888;">Vacancy ID:</span>
                                <span style="color: #2c3e50;">%s</span>
                              </div>
                              <div style="margin-bottom: 16px;">
                                <span style="color: #888;">Vacancy title:</span>
                                <span style="color: #2c3e50;">%s</span>
                              </div>
                              <div style="margin-bottom: 24px;">
                                <span style="color: #888;">Vacancy website:</span><br/>
                                <a href="%s" style="color: #fff; background: #3476de; padding: 8px 20px; border-radius: 8px; text-decoration: none; font-weight: 600; display: inline-block; margin-top: 6px;">Open Vacancy</a>
                              </div>
                              <div style="margin-bottom: 10px;">
                                <span style="color: #888;">Action:</span>
                                <span style="color: #222;">%s</span>
                              </div>
                            </div>
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
