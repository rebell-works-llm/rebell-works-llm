package com.rebellworksllm.backend.email.application;

import com.rebellworksllm.backend.email.application.exception.EmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("rebellworksllm@gmail.com");

        try {
            mailSender.send(message);
            logger.info("E-mail succesvol verzonden");
        } catch (Exception e) {
            logger.error("Fout bij verzenden e-mail, error: {}", e.getMessage(), e);
            throw new EmailException("Fout bij verzenden e-mail: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendWithHtml(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);
            logger.info("HTML e-mail succesvol verzonden");
        } catch (MessagingException e) {
            logger.error("Fout bij verzenden html e-mail, error: {}", e.getMessage(), e);
            throw new EmailException("Fout bij verzenden HTML e-mail: " + e.getMessage(), e);
        }
    }
}
