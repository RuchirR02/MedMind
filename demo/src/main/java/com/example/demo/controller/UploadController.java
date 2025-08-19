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
import java.util.*;
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
    public ResponseEntity<?> uploadPrescription(
            @RequestParam("prescription") MultipartFile file,
            @RequestParam("userId") String userId) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No file selected"));
        }

        try {
            File savedFile = File.createTempFile("prescription-", ".jpg");
            file.transferTo(savedFile);

            String extractedText = geminiService.extractTextFromImage(savedFile);
            savedFile.delete();

            Pattern pattern = Pattern.compile("(?i)Medicine[:\\s]+([A-Za-z0-9\\- ]+)[,\\n\\r ]*Time[:\\s]+([0-9:AMPamp ]+)");
            Matcher matcher = pattern.matcher(extractedText);

            List<Medicine> savedList = new ArrayList<>();

            while (matcher.find()) {
                String medName = matcher.group(1).trim();
                String medTime = normalizeTo24h(matcher.group(2).trim());

                Medicine medicine = new Medicine();
                medicine.setName(medName);
                medicine.setTime(medTime);
                medicine.setUserId(userId);  // ✅ link to current user

                savedList.add(medicineRepository.save(medicine));
            }

            return ResponseEntity.ok(Map.of(
                    "extractedText", extractedText,
                    "savedMedicines", savedList
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Internal server error",
                    "details", e.getMessage()
            ));
        }
    }

    private String normalizeTo24h(String input) {
        try {
            String cleaned = input.toUpperCase().trim()
                    .replace('\u00A0', ' ')
                    .replaceAll("\\s+", " ");
            if (cleaned.contains("AM") || cleaned.contains("PM")) {
                return LocalTime.parse(cleaned, FORMAT_12H).format(FORMAT_24H);
            } else {
                return LocalTime.parse(cleaned, DateTimeFormatter.ofPattern("H:mm")).format(FORMAT_24H);
            }
        } catch (DateTimeParseException e) {
            System.err.println("⚠️ Could not parse time: " + input);
            return "Unknown";
        }
    }
}
