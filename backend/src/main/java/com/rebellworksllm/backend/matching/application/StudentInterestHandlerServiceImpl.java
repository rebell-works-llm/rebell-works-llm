package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.email.application.EmailService;
import com.rebellworksllm.backend.hubspot.application.HubSpotStudentProvider;
import com.rebellworksllm.backend.hubspot.application.dto.StudentContact;
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
    private final String mailTo;

    public StudentInterestHandlerServiceImpl(HubSpotStudentProvider studentProvider,
                                             EmailService emailService,
                                             @Value("${mail.to}") String mailTo
    ) {
        this.studentProvider = studentProvider;
        this.emailService = emailService;
        this.mailTo = mailTo;
    }

    @Override
    public void handleReply(ContactResponseMessage responseMessage) {
        StudentContact studentContact = studentProvider.getStudentByPhone(responseMessage.contactPhone());
        logger.info("Interested student found: {}", studentContact.fullName());

        emailService.send(mailTo, "Student interest", "student:" + studentContact.fullName());
        logger.info("Email successfully sent to: {}", studentContact.fullName());

    }
}
