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

    // ✅ Fetch only for current user
    @GetMapping("/{userId}")
    public List<Medicine> getAllMedicines(@PathVariable String userId) {
        return medicineRepository.findByUserId(userId);
    }

    // ✅ Add new medicine with userId
    @PostMapping
    public Medicine addMedicine(@RequestBody Medicine medicine) {
        return medicineRepository.save(medicine);
    }
}
