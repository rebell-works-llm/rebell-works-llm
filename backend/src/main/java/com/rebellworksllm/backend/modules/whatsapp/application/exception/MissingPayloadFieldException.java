package com.rebellworksllm.backend.modules.whatsapp.application.exception;

public class MissingPayloadFieldException extends RuntimeException {

    public MissingPayloadFieldException(String message) {
        super(message);
    }

}
