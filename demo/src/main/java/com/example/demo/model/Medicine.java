package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Entity
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Always store in 24-hour HH:mm
    private String time;

    private static final DateTimeFormatter FORMAT_24H = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FORMAT_12H = DateTimeFormatter.ofPattern("hh:mm a");

    public Medicine() {}

    // ✅ Add constructor that normalizes automatically
    public Medicine(String name, String time) {
        this.name = name;
        this.time = normalizeTime(time);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    // ✅ Always normalize before saving
    public void setTime(String time) {
        this.time = normalizeTime(time);
    }

    private String normalizeTime(String timeInput) {
        if (timeInput == null || timeInput.trim().isEmpty()) return null;

        // Replace multiple spaces, non-breaking spaces, tabs with a single normal space
        String normalized = timeInput.replaceAll("\\s+", " ").replace('\u00A0', ' ').trim().toUpperCase();

        try {
            if (normalized.contains("AM") || normalized.contains("PM")) {
                return LocalTime.parse(normalized, FORMAT_12H).format(FORMAT_24H);
            }
            return LocalTime.parse(normalized, FORMAT_24H).format(FORMAT_24H);
        } catch (DateTimeParseException e) {
            System.err.println("⚠️ Could not parse time: '" + timeInput + "' (" + e.getMessage() + ")");
            return "Unknown"; // fallback
        }
    }

}
