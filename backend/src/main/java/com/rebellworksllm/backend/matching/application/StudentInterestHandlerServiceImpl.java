package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.email.application.EmailService;
import com.rebellworksllm.backend.hubspot.application.HubSpotStudentProvider;
import com.rebellworksllm.backend.hubspot.application.dto.StudentContact;
import com.rebellworksllm.backend.whatsapp.application.dto.ContactResponseMessage;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
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

        String htmlBody = String.format("""
                        <html>
                        <body style="font-family: Arial, sans-serif; color: #333;">
                            <h2 style="color: #2c3e50;">📩 New Student Interest</h2>

                            <p><strong>👤 Name:</strong> %s</p>
                            <p><strong>🎓 Study:</strong> %s</p>
                            <p><strong>📍 Location:</strong> %s</p>
                            <p><strong>🎓 Graduation:</strong> %s</p>

                            <p><strong>📝 Motivation:</strong><br/>%s</p>

                            <p><strong>☎️ Phone:</strong> %s</p>
                            <p><strong>📩 Message:</strong><br/>"%s"</p>
                        </body>
                        </html>
                        """,
                studentContact.fullName(),
                studentContact.study(),
                studentContact.studyLocation(),
                studentContact.expectedGraduationDate(),
                studentContact.text(),
                responseMessage.contactPhone(),
                responseMessage.message()
        );

        emailService.sendWithHtml(mailTo, "New Student Interest", htmlBody);
        logger.info("Email successfully sent to: {}", studentContact.fullName());

    }
}
