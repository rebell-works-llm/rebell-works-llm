package com.rebellworksllm.backend.common.utils;

import org.apache.commons.text.StringEscapeUtils;

public class TextUtils {

    private TextUtils() {
    }

    /**
     * Clean and escape text parameter for WhatsApp template.
     */
    public static String checkAndCleanText(final String input) {
        if (input == null || input.trim().isEmpty()) {
            return "Unknown";
        }

        String cleaned = input.replaceAll("[\\n\\t\\r]+", " ")
                .replaceAll(" {2,}", " ")
                .trim();

        return StringEscapeUtils.escapeJson(cleaned);
    }
}
