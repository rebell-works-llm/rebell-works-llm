package com.rebellworksllm.backend.modules.matching.application;


import com.rebellworksllm.backend.modules.whatsapp.application.dto.ContactResponseMessage;

public interface StudentInterestHandlerService {

    void handleReply(ContactResponseMessage responseMessage);
}
