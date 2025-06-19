package com.rebellworksllm.backend.email.application;

public interface EmailService {

    void sendFallbackEmail(String to, String subject, String body);

}
