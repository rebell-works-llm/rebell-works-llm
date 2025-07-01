package com.rebellworksllm.backend.modules.email.application;

import com.rebellworksllm.backend.modules.email.application.EmailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final EmailService emailService;

    public TestController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/send-test")
    public String sendTestEmail() {
        emailService.sendWithHtml(
                "thephilannguyen@gmail.com",
                "Test Email",
                "<h1>Hello!</h1><p>This is a test email from Rebell Works.</p>"
        );
        return "Sent!";
    }
}