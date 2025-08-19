import React, { useState } from "react";
import api from "../services/api";

export default function PrescriptionUpload({ onAdded, userId }) {
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);
  const [notifLoading, setNotifLoading] = useState(false);

  async function handleUpload(e) {
    e.preventDefault();
    if (!file) {
      alert("Please select a file");
      return;
    }

    const formData = new FormData();
    formData.append("prescription", file);
    formData.append("userId", userId); // ✅ Added userId

    try {
      setLoading(true);
      const res = await api.post("/api/upload", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });

      console.log("Upload response:", res.data);
      setResult(res.data);

      setFile(null);

      // ✅ Refresh schedule immediately
      if (onAdded) onAdded();
    } catch (err) {
      console.error(err);
      alert("Failed to upload prescription");
    } finally {
      setLoading(false);
    }
  }

  async function handleSendNotification() {
    try {
      setNotifLoading(true);
      const res = await api.sendTestNotification();
      alert("✅ Notification triggered: " + res.data);
    } catch (err) {
      console.error(err);
      alert("❌ Failed to send notification");
    } finally {
      setNotifLoading(false);
    }
  }

  return (
    <div>
      <form onSubmit={handleUpload}>
        <input
          type="file"
          accept="image/*"
          onChange={(e) => setFile(e.target.files[0])}
        />
        <button type="submit" disabled={loading}>
          {loading ? "Uploading..." : "Upload"}
        </button>
      </form>

      <button
        onClick={handleSendNotification}
        disabled={notifLoading}
        style={{ marginTop: "1rem" }}
      >
        {notifLoading ? "Sending..." : "Send Test Notification"}
      </button>
    </div>
  );
}
