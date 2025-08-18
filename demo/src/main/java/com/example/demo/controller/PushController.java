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

    private final List<String> subscriptions = new ArrayList<>();

    public List<String> getSubscriptions() {
        return subscriptions;
    }

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    // ✅ Base64URL-safe decode
    public static byte[] base64UrlDecode(String key) {
        return Base64.getUrlDecoder().decode(key);
    }

    @GetMapping("/vapidPublicKey")
    public ResponseEntity<?> getPublicKey() {
        return ResponseEntity.ok(new JSONObject().put("vapidPublicKey", publicKey).toString());
    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody String subscriptionJson) {
        subscriptions.add(subscriptionJson);
        System.out.println("Saved subscription: " + subscriptionJson);
        return ResponseEntity.ok("Subscription saved");
    }

    @PostMapping("/sendTestNotification")
    public ResponseEntity<?> sendTestNotification() {
        try {
            PushService pushService = new PushService();
            pushService.setPublicKey(Utils.loadPublicKey(base64UrlDecode(publicKey)));
            pushService.setPrivateKey(Utils.loadPrivateKey(base64UrlDecode(privateKey)));

            for (String subJson : subscriptions) {
                JSONObject sub = new JSONObject(subJson);

                String payload = new JSONObject()
                        .put("title", "MedMind Reminder")
                        .put("body", "💊 Time to take your medicine!")
                        .toString();

                Notification notification = new Notification(
                        sub.getString("endpoint"),
                        sub.getJSONObject("keys").getString("p256dh"),
                        sub.getJSONObject("keys").getString("auth"),
                        payload
                );

                pushService.send(notification);
            }

            return ResponseEntity.ok("Test notifications sent ✅");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Error sending test notification: " + e.getMessage());
        }
    }
}
