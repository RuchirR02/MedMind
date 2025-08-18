package com.example.demo.controller;

import com.example.demo.model.Medicine;
import com.example.demo.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
@CrossOrigin(origins = "http://localhost:5173")
public class MedicineController {

    @Autowired
    private MedicineRepository medicineRepository;

    @GetMapping
    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    @PostMapping
    public Medicine addMedicine(@RequestBody Medicine medicine) {
        String rawTime = medicine.getTime();
        String normalized = normalizeTime(rawTime);
        medicine.setTime(normalized);

        Medicine saved = medicineRepository.save(medicine);
        System.out.println("📥 Incoming: " + rawTime + " | 💾 Stored: " + saved.getTime());
        return saved;
    }

    private String normalizeTime(String timeInput) {
        if (timeInput == null || timeInput.trim().isEmpty()) return null;

        try {
            // Case: "10:23 PM"
            if (timeInput.toUpperCase().contains("AM") || timeInput.toUpperCase().contains("PM")) {
                return java.time.LocalTime.parse(
                        timeInput.toUpperCase(),
                        java.time.format.DateTimeFormatter.ofPattern("hh:mm a")
                ).format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
            }

            // Case: "9:09", "09:09", "21:47"
            return java.time.LocalTime.parse(
                    timeInput,
                    java.time.format.DateTimeFormatter.ofPattern("H:mm")
            ).format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) {
            System.err.println("⚠️ Could not parse time: " + timeInput);
            return timeInput; // fallback
        }
    }

}
