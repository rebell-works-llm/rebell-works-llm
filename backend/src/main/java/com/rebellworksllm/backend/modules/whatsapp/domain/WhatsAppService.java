package com.rebellworksllm.backend.modules.whatsapp.domain;

import java.util.List;

public interface WhatsAppService {
    void sendTemplateMessage(String phoneNumber,
                             String templateName,
                             String languageCode,
                             List<String> parameters);
}