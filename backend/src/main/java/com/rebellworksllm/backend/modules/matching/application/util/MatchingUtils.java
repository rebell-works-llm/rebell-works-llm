package com.rebellworksllm.backend.modules.matching.application.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MatchingUtils {

    private MatchingUtils() {
    }

    public static String normalizePhone(String phone) {
        if (phone == null) return null;

        // keep digits only
        String digits = phone.replaceAll("\\D", "");

        if (digits.isBlank()) return null;

        // 0031... => 31...
        if (digits.startsWith("0031")) {
            digits = "31" + digits.substring(4);
        }

        // 06... (or other 0...) => 31...
        if (digits.startsWith("0") && digits.length() > 1) {
            digits = "31" + digits.substring(1);
        }

        return digits;
    }

    public static List<String> createDutchPhoneVariantsForHubSpot(String phone) {
        String normalized = normalizePhone(phone);
        if (normalized == null) return List.of();

        Set<String> variants = new LinkedHashSet<>();

        // Always include international forms
        variants.add(normalized);         // 31...
        variants.add("+" + normalized);   // +31...

        // If it's Dutch mobile (316...), also include local 06...
        if (normalized.startsWith("316") && normalized.length() > 3) {
            variants.add("06" + normalized.substring(3));
        }

        return new ArrayList<>(variants);
    }
}
