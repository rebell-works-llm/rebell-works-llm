package com.rebellworksllm.backend.whatsapp.application;

public interface WhatsAppService {

    void sendWithVacancyTemplate(String phoneNumber,
                                   String name,
                                   String vac1,
                                   String vac2,
                                   String vac3,
                                   String vac4,
                                   String vac5);

}
