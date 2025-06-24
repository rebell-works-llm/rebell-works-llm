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

    public static String sanitize(String input) {
        if (input == null) return "Unknown";

        String safe = input.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
        safe = safe.trim().replaceAll(" {2,}", " ");
        // Remove non-latin (replace with "")
        safe = safe.replaceAll("[^\\p{IsLatin}\\p{IsDigit}\\p{Punct}\\s]", "");
        // Collapse whitespace
        safe = safe.replaceAll("\\s+", " ");
        return safe.isBlank() ? "Unknown" : safe;
    }

    public static String capLength(String input, int maxLength) {
        if (input.length() > maxLength) {
            return input.substring(0, Math.max(0, maxLength)) + "...";
        }
        return input;
    }
}
