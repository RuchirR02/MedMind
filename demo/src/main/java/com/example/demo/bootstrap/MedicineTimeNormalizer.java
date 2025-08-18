package com.example.demo.bootstrap;

import com.example.demo.model.Medicine;
import com.example.demo.repository.MedicineRepository;
import com.example.demo.util.TimeUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MedicineTimeNormalizer implements CommandLineRunner {

    private final MedicineRepository repo;

    public MedicineTimeNormalizer(MedicineRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        List<Medicine> all = repo.findAll();
        boolean dirty = false;

        for (Medicine m : all) {
            String t = m.getTime();
            if (t == null || t.isBlank()) continue;
            try {
                String norm = TimeUtil.normalizeTo24h(t);
                if (!norm.equals(t)) {
                    System.out.println("🔧 Normalizing time for " + m.getName() + ": '" + t + "' → '" + norm + "'");
                    m.setTime(norm);
                    dirty = true;
                }
            } catch (Exception ignored) {
                // Leave invalid as-is; ReminderService logs it when encountered
            }
        }
        if (dirty) repo.saveAll(all);
    }
}
