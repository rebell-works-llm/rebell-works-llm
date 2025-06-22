package com.rebellworksllm.backend.modules.email.application;

public interface EmailService {

    void send(String to, String subject, String body);

    void sendWithHtml(String to, String subject, String body);

}
