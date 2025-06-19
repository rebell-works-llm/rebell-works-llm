package com.rebellworksllm.backend.email;

public interface EmailService {

    void sendFallbackEmail(String to, String subject, String body);

}
