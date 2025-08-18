package com.example.demo.controller;

import com.example.demo.model.Medicine;
import com.example.demo.repository.MedicineRepository;
import com.example.demo.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.regex.*;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "http://localhost:5173")
public class UploadController {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private MedicineRepository medicineRepository;

    private static final DateTimeFormatter FORMAT_24H = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FORMAT_12H = DateTimeFormatter.ofPattern("hh:mm a");

    @PostMapping
    public ResponseEntity<?> uploadPrescription(@RequestParam("prescription") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No file selected"));
        }

        try {
            File savedFile = File.createTempFile("prescription-", ".jpg");
            file.transferTo(savedFile);

            String extractedText = geminiService.extractTextFromImage(savedFile);
            savedFile.delete();

            String medName = "Unknown";
            String medTime = "08:00"; // fallback default

            // Extract medicine + time from Gemini response
            Pattern pattern = Pattern.compile("(?i)Medicine[:\\s]+([A-Za-z0-9\\- ]+)[,\\n\\r ]*Time[:\\s]+([0-9:AMPamp ]+)");
            Matcher matcher = pattern.matcher(extractedText);

            if (matcher.find()) {
                medName = matcher.group(1).trim();
                medTime = normalizeTo24h(matcher.group(2).trim()); // ✅ normalize right here
            }

            // Medicine medicine = new Medicine();
            // medicine.setName(medName);
            // medicine.setTime(medTime); // ✅ always stored as 24h

            // Medicine saved = medicineRepository.save(medicine);
            Medicine medicine = new Medicine(medName, medTime); // ✅ will normalize inside constructor
            Medicine saved = medicineRepository.save(medicine);


            return ResponseEntity.ok(Map.of(
                    "extractedText", extractedText,
                    "savedMedicine", saved
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Internal server error",
                    "details", e.getMessage()
            ));
        }
    }

    // ✅ Convert any "10:16 PM" → "22:16"
    private String normalizeTo24h(String input) {
        try {
            String cleaned = input.toUpperCase().trim();

            if (cleaned.contains("AM") || cleaned.contains("PM")) {
                return LocalTime.parse(cleaned, FORMAT_12H).format(FORMAT_24H);
            } else {
                return LocalTime.parse(cleaned, FORMAT_24H).format(FORMAT_24H);
            }
        } catch (DateTimeParseException e) {
            System.err.println("⚠️ Could not parse time: " + input);
            return "08:00"; // fallback default
        }
    }
}
