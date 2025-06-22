package com.rebellworksllm.backend.common.utils;

public class LogUtils {

    private final static int PHONE_MASK_LENGTH = 4;

    public static String maskPhone(String text) {
        if (text.length() <= PHONE_MASK_LENGTH) {
            return "";
        }

        return text.replaceAll("\\d{" + PHONE_MASK_LENGTH + "}$", "****");
    }

    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "";
        }

        String[] parts = email.split("@");
        String name = parts[0];
        String domain = parts[1];

        if (name.length() <= 2) {
            return "***@" + domain;
        }

        String visiblePart = name.substring(0, 2);
        return visiblePart + "****@" + domain;
    }
}
