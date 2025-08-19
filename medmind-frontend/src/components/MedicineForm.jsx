import React, { useState } from "react";
import api from "../services/api";
import { getUserId } from "../utils/userId";

export default function MedicineForm({ onAdded }) {
  const [name, setName] = useState("");
  const [time, setTime] = useState("");

  async function handleSubmit(e) {
    e.preventDefault();
    if (!name || !time) {
      alert("Please fill all fields");
      return;
    }
    try {
      await api.post("/api/medicines", { name, time, userId: getUserId() });
      setName("");
      setTime("");
      if (onAdded) onAdded();
    } catch (err) {
      console.error(err);
      alert("Failed to add medicine");
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <div>
        <label>Medicine Name</label>
        <input
          type="text"
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder="e.g. Paracetamol"
        />
      </div>

      <div>
        <label>Time</label>
        <input
          type="time"
          value={time}
          onChange={(e) => setTime(e.target.value)}
        />
      </div>

      <button type="submit">Add Medicine</button>
    </form>
  );
}
