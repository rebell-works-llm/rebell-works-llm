package com.rebellworksllm.backend.whatsApp.application;
import io.github.cdimascio.dotenv.Dotenv;

public class EnvLoader {

    private static Dotenv dotenv = Dotenv.load();

    public static String getWhatsAppToken() {
        return dotenv.get("WHATSAPP_API_TOKEN");
    }

    public static String getPhoneNumberId() {
        return dotenv.get("WHATSAPP_PHONE_NUMBER_ID");
    }


}
