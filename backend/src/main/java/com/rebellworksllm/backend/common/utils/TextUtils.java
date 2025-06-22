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

    public static String sanitize(String input, int maxLength) {
        if (input == null) return "Unknown";
        String safe = input.trim();
        // Escape HTML and common injection vectors
        safe = StringEscapeUtils.escapeHtml4(safe);
        // Remove non-latin (replace with "")
        safe = safe.replaceAll("[^\\p{IsLatin}\\p{IsDigit}\\p{Punct}\\s]", "");
        // Collapse whitespace
        safe = safe.replaceAll("\\s+", " ");
        // Cap length
        if (safe.length() > maxLength) {
            safe = safe.substring(0, maxLength - 3) + "...";
        }
        return safe.isBlank() ? "Unknown" : safe;
    }
}
