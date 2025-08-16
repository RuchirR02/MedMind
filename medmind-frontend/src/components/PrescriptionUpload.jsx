import React, { useState } from "react";
import api from "../services/api";

export default function PrescriptionUpload({ onAdded }) {
  const [file, setFile] = useState(null);

  async function handleUpload(e) {
    e.preventDefault();
    if (!file) {
      alert("Please select a file");
      return;
    }
    const formData = new FormData();
    formData.append("prescription", file);

    try {
      const res = await api.post("/api/upload", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });

      console.log("✅ Upload response:", res.data);
      setFile(null);
      if (onAdded) onAdded();
      alert("Medicine added from prescription!");
    } catch (err) {
      console.error("API Error:", err);
      alert(err.response?.data?.error || "Failed to upload prescription");
    }
  }

  return (
    <form onSubmit={handleUpload}>
      <input
        type="file"
        accept="image/*"
        onChange={(e) => setFile(e.target.files[0])}
      />
      <button type="submit">Upload</button>
    </form>
  );
}
