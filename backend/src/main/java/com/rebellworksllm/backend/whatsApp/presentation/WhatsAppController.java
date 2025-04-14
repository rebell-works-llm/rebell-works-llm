package com.rebellworksllm.backend.whatsApp.presentation;

import com.rebellworksllm.backend.whatsApp.application.WhatsAppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/whatsAppSend")
public class WhatsAppController {

    private final WhatsAppService whatsAppService;


    public WhatsAppController(WhatsAppService whatsAppService){
        this.whatsAppService = whatsAppService;
    }


    @PostMapping
    public ResponseEntity<Void> sendMessage(@RequestBody WhatsAppDTO phoneNumber) {
        try {
            whatsAppService.sendFirstMessage(phoneNumber.phoneNumber());
            return ResponseEntity.ok().build();
        } catch (Exception e) {

            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}
