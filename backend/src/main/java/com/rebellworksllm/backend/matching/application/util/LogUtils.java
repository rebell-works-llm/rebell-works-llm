package com.rebellworksllm.backend.matching.application.util;

public class LogUtils {

    private final static int PHONE_MASK_LENGTH = 4;

    public static String maskPhone(String text) {
        if (text.length() <= PHONE_MASK_LENGTH) {
            return "";
        }

        return text.replaceAll("\\d{" + PHONE_MASK_LENGTH + "}$", "****");
    }
}
