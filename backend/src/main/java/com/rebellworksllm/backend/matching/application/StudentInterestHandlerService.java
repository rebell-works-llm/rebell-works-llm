package com.rebellworksllm.backend.matching.application;


import com.rebellworksllm.backend.whatsapp.application.dto.ContactResponseMessage;

public interface StudentInterestHandlerService {

    void handleReply(ContactResponseMessage responseMessage);
}
