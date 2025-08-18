package com.example.demo.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public final class TimeUtil {

    private TimeUtil() {}

    // Try a few common patterns (12h & 24h, with/without seconds, with dots)
    private static final DateTimeFormatter[] CANDIDATES = new DateTimeFormatter[]{
            DateTimeFormatter.ofPattern("H:mm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("HH:mm:ss", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("h:mm:ss a", Locale.ENGLISH),
            // tolerate "9.30 pm"
            DateTimeFormatter.ofPattern("h.mm a", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("hh.mm a", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("H.mm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("HH.mm", Locale.ENGLISH)
    };

    /** Parse flexible inputs like "21:30", "9:30 PM", "09:30 pm", "09:30:00", "9.30 pm". */
    public static LocalTime parseFlexible(String raw) {
        if (raw == null) throw new IllegalArgumentException("Time is null");

        // Normalize a bit: trim, collapse spaces, make AM/PM consistent, replace '.' with ':'
        String v = raw.trim()
                .replaceAll("\\s+", " ")
                .replace('.', ':')
                .toUpperCase(Locale.ENGLISH);

        for (DateTimeFormatter f : CANDIDATES) {
            try { return LocalTime.parse(v, f); }
            catch (DateTimeParseException ignored) {}
        }
        throw new IllegalArgumentException("Unrecognized time format: " + raw);
    }

    /** Format to strict 24h "HH:mm" (no seconds). */
    public static String to24h(LocalTime t) {
        return t.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    /** Convenience: parse anything -> "HH:mm". */
    public static String normalizeTo24h(String input) {
        return to24h(parseFlexible(input));
    }
}
