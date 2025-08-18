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
        // IMPORTANT: use save24 so "09:30 PM" becomes "21:30"
        return medicineRepository.save24(medicine);
    }
}
