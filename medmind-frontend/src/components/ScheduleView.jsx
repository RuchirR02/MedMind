import React, { useEffect, useState } from "react";
import api from "../services/api";
import { getUserId } from "../utils/userId";

export default function ScheduleView() {
  const [medicines, setMedicines] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchMedicines() {
      try {
        const res = await api.get(`/api/medicines/${getUserId()}`);
        setMedicines(res.data);
      } catch (err) {
        console.error("Failed to fetch medicines:", err);
      } finally {
        setLoading(false);
      }
    }
    fetchMedicines();
  }, []);

  if (loading) {
    return <p>Loading your schedule...</p>;
  }

  if (!medicines.length) {
    return <p>No medicines scheduled.</p>;
  }

  return (
    <table border="1" cellPadding="8" style={{ width: "100%" }}>
      <thead>
        <tr>
          <th>Name</th>
          <th>Time</th>
        </tr>
      </thead>
      <tbody>
        {medicines.map((med) => (
          <tr key={med.id}>
            <td>{med.name}</td>
            <td>{med.time}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
