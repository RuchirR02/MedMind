package com.example.demo.controller;

import com.example.demo.model.Medicine;
import com.example.demo.repository.MedicineRepository;
import com.example.demo.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class UploadController {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private MedicineRepository medicineRepository;

    // ✅ Upload prescription
    @PostMapping("/upload")
    public ResponseEntity<?> uploadPrescription(@RequestParam("prescription") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No file selected"));
        }

        try {
            String filename = System.currentTimeMillis() + "-" + file.getOriginalFilename().replaceAll("\\s+", "_");
            File tempDir = new File(System.getProperty("java.io.tmpdir"), "uploads");
            if (!tempDir.exists()) tempDir.mkdirs();
            File savedFile = new File(tempDir, filename);
            file.transferTo(savedFile);

            // Call Gemini
            String extractedText = geminiService.extractTextFromImage(savedFile);
            savedFile.delete();

            // Simple parsing
            String medName = extractedText.contains("Medicine") ? extractedText.split("Medicine")[1].split("Time")[0].trim() : "Unknown";
            String medTime = extractedText.contains("Time") ? extractedText.split("Time")[1].trim() : "Unknown";

            Medicine medicine = new Medicine(medName, medTime);
            Medicine saved = medicineRepository.save(medicine);

            return ResponseEntity.ok(Map.of(
                    "extractedText", extractedText,
                    "savedMedicine", saved
            ));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "File processing failed", "details", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error", "details", e.getMessage()));
        }
    }

    // ✅ Manual entry
    @PostMapping("/medicines")
    public ResponseEntity<?> addMedicine(@RequestBody Medicine medicine) {
        Medicine saved = medicineRepository.save(medicine);
        return ResponseEntity.ok(saved);
    }

    // ✅ Fetch all
    @GetMapping("/medicines")
    public ResponseEntity<?> getAllMedicines() {
        return ResponseEntity.ok(medicineRepository.findAll());
    }
}
