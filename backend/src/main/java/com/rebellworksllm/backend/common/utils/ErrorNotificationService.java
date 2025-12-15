package com.rebellworksllm.backend.common.utils;

import com.rebellworksllm.backend.modules.email.application.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;

@Service
public class ErrorNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(ErrorNotificationService.class);

    private final EmailService emailService;

    @Value("${mail.to.errors}")
    private String errorMailTo;

    public ErrorNotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void sendErrorEmail(String subjectPrefix, String contextInfo, Exception e) {
        String subject = "ERROR - " + subjectPrefix + " failed";
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));

        String body = String.format("""
                        ERROR in %s
                        
                        %s
                        
                        Exception:
                        - type: %s
                        - message: %s
                        
                        Stacktrace:
                        %s
                        """,
                subjectPrefix,
                contextInfo,
                e.getClass().getName(),
                e.getMessage(),
                sw.toString()
        );

        try {
            emailService.send(errorMailTo, subject, body);
            logger.info("Error email sent to {} for {}", LogUtils.maskEmail(errorMailTo), subjectPrefix);
        } catch (Exception mailException) {
            logger.error("Failed to send error email to {}", LogUtils.maskEmail(errorMailTo), mailException);
        }
    }
}
