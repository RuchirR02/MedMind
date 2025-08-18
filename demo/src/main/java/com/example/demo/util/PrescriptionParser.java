package com.example.demo.util;

import java.util.regex.*;
import java.util.*;

public class PrescriptionParser {

    public static Map<String, String> parseMedicine(String extractedText) {
        Map<String, String> result = new HashMap<>();

        // ✅ Normalize text
        String clean = extractedText.replaceAll("[^a-zA-Z0-9: ]", "").trim();
        // → turns "Time 12:09 PM ?" → "Time 1209 PM"

        // ✅ Medicine name
        Pattern medPattern = Pattern.compile("(?i)(?:medicine name[:\\s\\-]*)([a-zA-Z0-9 ]+)");
        Matcher medMatcher = medPattern.matcher(clean);
        if (medMatcher.find()) {
            result.put("name", medMatcher.group(1).trim());
        } else {
            result.put("name", "Unknown");
        }

        // ✅ Time
        Pattern timePattern = Pattern.compile(
            "(\\b\\d{1,2}:\\d{2}\\s?(?:AM|PM)?\\b|\\b\\d{1,2}\\s?(?:AM|PM)\\b)",
            Pattern.CASE_INSENSITIVE
        );
        Matcher timeMatcher = timePattern.matcher(clean);
        if (timeMatcher.find()) {
            result.put("time", timeMatcher.group(1).trim().toUpperCase());
        } else {
            result.put("time", "Unknown"); // fallback default
        }

        return result;
    }
}
