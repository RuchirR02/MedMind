package com.example.demo.controller;   // for controller files
//package com.example.demo.service;      // for service files
//package com.example.demo.model;        // for model files


import com.example.demo.model.Medicine;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/medicines")
@CrossOrigin(origins = "http://localhost:5173")
public class MedicineController {

    private final List<Medicine> medicines = new ArrayList<>();

    @GetMapping
    public List<Medicine> getMedicines() {
        return medicines;
    }

    @PostMapping
    public ResponseEntity<?> addMedicine(@RequestBody Medicine medicine) {
        medicines.add(medicine);
        return ResponseEntity.ok().build();
    }
}
