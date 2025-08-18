package com.example.demo.repository;

import com.example.demo.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    /**
     * Save while forcing time into 24h (HH:mm).
     * Use this in controllers/services instead of save().
     */
    default <S extends Medicine> S save24(S entity) {
        if (entity != null) {
            entity.setTime(normalizeTo24(entity.getTime()));
        }
        // Debug: print what’s being saved
        System.out.println("[repo] saving => name=" + entity.getName() + ", time=" + entity.getTime());
        return save(entity);
    }

    // --- helpers ---

    private static String normalizeTo24(String input) {
        if (input == null) return null;

        // Clean weird spaces from OCR (e.g., non-breaking space)
        String cleaned = input.replace('\u00A0', ' ')
                              .replaceAll("\\s+", " ")
                              .trim()
                              .toUpperCase();

        DateTimeFormatter HHmm = DateTimeFormatter.ofPattern("HH:mm");
        try {
            // Case A: "h:mm AM/PM"  (accepts 1 or 2 digit hour)
            if (cleaned.matches("^\\d{1,2}:\\d{2}\\s?(AM|PM)$")) {
                LocalTime t = LocalTime.parse(cleaned, DateTimeFormatter.ofPattern("h:mm a"));
                return t.format(HHmm);
            }

            // Case B: "h AM/PM" (no minutes) → add :00
            if (cleaned.matches("^\\d{1,2}\\s?(AM|PM)$")) {
                LocalTime t = LocalTime.parse(cleaned.replace(" ", ":00 "),
                        DateTimeFormatter.ofPattern("h:mm a"));
                return t.format(HHmm);
            }

            // Case C: already 24h like "21:30" or "9:05" (single-digit hour)
            if (cleaned.matches("^\\d{1,2}:\\d{2}$")) {
                LocalTime t = LocalTime.parse(cleaned, DateTimeFormatter.ofPattern("H:mm"));
                return t.format(HHmm);
            }

            // If none matched, leave it as is (but you’ll see the log)
            System.err.println("[repo] normalizeTo24: unrecognized time '" + input + "'");
            return input;
        } catch (Exception e) {
            System.err.println("[repo] normalizeTo24 FAILED for '" + input + "': " + e.getMessage());
            return input; // fallback: store as-is so you can see it and fix parser if needed
        }
    }
}
