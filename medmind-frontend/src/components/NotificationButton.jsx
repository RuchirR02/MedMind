import React from "react";

const NotificationButton = () => {
  const enableNotifications = async () => {
    if (!("Notification" in window) || !("serviceWorker" in navigator)) {
      alert("Notifications are not supported in this browser.");
      return;
    }

    const permission = await Notification.requestPermission();
    if (permission !== "granted") {
      alert("Notifications are disabled.");
      return;
    }

    try {
      // Register your existing service worker
      const reg = await navigator.serviceWorker.register("/serviceWorker.js");
      console.log("✅ Service Worker registered:", reg);

      // Subscribe to push notifications
      const subscription = await reg.pushManager.subscribe({
        userVisibleOnly: true,
        applicationServerKey: urlBase64ToUint8Array(import.meta.env.VITE_VAPID_PUBLIC_KEY)
      });

      console.log("📩 Subscription object:", subscription);

      // Send subscription to backend
      const res = await fetch("http://localhost:8081/api/subscribe", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(subscription)
      });

      if (res.ok) {
        alert("✅ Notifications enabled!");
      } else {
        alert("⚠️ Failed to save subscription on server.");
      }
    } catch (err) {
      console.error("❌ Error enabling notifications:", err);
    }
  };

  // Helper: convert VAPID public key to Uint8Array
  const urlBase64ToUint8Array = (base64String) => {
    const padding = "=".repeat((4 - (base64String.length % 4)) % 4);
    const base64 = (base64String + padding)
      .replace(/-/g, "+")
      .replace(/_/g, "/");
    const rawData = window.atob(base64);
    const outputArray = new Uint8Array(rawData.length);
    for (let i = 0; i < rawData.length; ++i) {
      outputArray[i] = rawData.charCodeAt(i);
    }
    return outputArray;
  };

  return (
    <button
      onClick={enableNotifications}
      style={{
        padding: "10px 18px",
        backgroundColor: "#1976d2",
        color: "#fff",
        border: "none",
        borderRadius: "5px",
        cursor: "pointer"
      }}
    >
      Enable Reminders
    </button>
  );
};

export default NotificationButton;
