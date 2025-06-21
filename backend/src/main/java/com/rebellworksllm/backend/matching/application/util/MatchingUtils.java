package com.rebellworksllm.backend.matching.application.util;

public class MatchingUtils {

    private MatchingUtils() {
    }

    public static String normalizePhone(String phone) {
        if (phone == null) return null;
        return phone.replace("+", "");
    }
}
