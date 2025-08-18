package com.example.demo.service;

import com.example.demo.model.Medicine;
import com.example.demo.repository.MedicineRepository;
import com.example.demo.util.TimeUtil;
import com.example.demo.controller.PushController;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Utils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.Security;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

@Service
public class ReminderService {

    private final MedicineRepository medicineRepository;
    private final PushController pushController;
    private final PushService pushService;

    private static final DateTimeFormatter LOG_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public ReminderService(MedicineRepository medicineRepository,
                           PushController pushController,
                           @Value("${vapid.public.key}") String publicKey,
                           @Value("${vapid.private.key}") String privateKey) throws Exception {
        this.medicineRepository = medicineRepository;
        this.pushController = pushController;

        Security.addProvider(new BouncyCastleProvider());

        this.pushService = new PushService();
        // IMPORTANT: Base64 URL decode VAPID keys
        this.pushService.setPublicKey(Utils.loadPublicKey(Base64.getUrlDecoder().decode(publicKey)));
        this.pushService.setPrivateKey(Utils.loadPrivateKey(Base64.getUrlDecoder().decode(privateKey)));
    }

    /** Runs every minute, compares times at minute precision. */
    @Scheduled(fixedRate = 60_000)
    public void checkMedicineReminders() {
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);
        String nowStr = now.format(LOG_FMT);
        System.out.println("⏰ Current time (24h): " + nowStr);

        List<Medicine> medicines = medicineRepository.findAll();

        for (Medicine medicine : medicines) {
            String raw = medicine.getTime();
            if (raw == null || raw.isBlank()) continue;

            try {
                LocalTime medTime = TimeUtil.parseFlexible(raw).withSecond(0).withNano(0);
                String medStr = medTime.format(LOG_FMT);

                System.out.println("➡️ Checking medicine: " + medicine.getName() + " at " + medStr);

                if (medTime.equals(now)) {
                    System.out.println("✅ Reminder matched for: " + medicine.getName());
                    sendReminder(medicine.getName());
                }
            } catch (Exception ex) {
                System.err.println("⚠️ Could not parse time '" + raw + "' for " + medicine.getName() + ": " + ex.getMessage());
            }
        }
    }

    private void sendReminder(String medicineName) {
        for (String subJson : pushController.getSubscriptions()) {
            try {
                JSONObject sub = new JSONObject(subJson);

                // Always send JSON payload (prevents SW .json() error)
                String payload = new JSONObject()
                        .put("title", "MedMind Reminder")
                        .put("body", "💊 Take: " + medicineName)
                        .toString();

                Notification notification = new Notification(
                        sub.getString("endpoint"),
                        sub.getJSONObject("keys").getString("p256dh"),
                        sub.getJSONObject("keys").getString("auth"),
                        payload
                );

                pushService.send(notification);
                System.out.println("📩 Sent notification for " + medicineName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
