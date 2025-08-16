package com.example.demo.controller;

import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Utils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Security;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class PushController {

    @Value("${vapid.public.key}")
    private String publicKey;

    @Value("${vapid.private.key}")
    private String privateKey;

    // TEMP in-memory list for subscriptions
    private final List<String> subscriptions = new ArrayList<>();

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @GetMapping("/vapidPublicKey")
    public ResponseEntity<?> getPublicKey() {
        return ResponseEntity.ok(new JSONObject().put("vapidPublicKey", publicKey).toString());
    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody String subscriptionJson) {
        subscriptions.add(subscriptionJson); // Save for testing
        System.out.println("Saved subscription: " + subscriptionJson);
        return ResponseEntity.ok("Subscription saved");
    }

    // ✅ Test endpoint to send a push notification to all stored subscriptions
    @PostMapping("/sendTestNotification")
    public ResponseEntity<?> sendTestNotification() {
        try {
            PushService pushService = new PushService();
            pushService.setPublicKey(Utils.loadPublicKey(Base64.getDecoder().decode(publicKey)));
            pushService.setPrivateKey(Utils.loadPrivateKey(Base64.getDecoder().decode(privateKey)));

            for (String subJson : subscriptions) {
                JSONObject sub = new JSONObject(subJson);
                Notification notification = new Notification(
                        sub.getString("endpoint"),
                        sub.getJSONObject("keys").getString("p256dh"),
                        sub.getJSONObject("keys").getString("auth"),
                        "💊 MedMind: Time to take your medicine!"
                );
                pushService.send(notification);
            }

            return ResponseEntity.ok("Test notifications sent to all subscribers");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error sending test notification: " + e.getMessage());
        }
    }
}
