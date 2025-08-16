import React, { useEffect, useState } from "react";
import MedicineForm from "./components/MedicineForm";
import PrescriptionUpload from "./components/PrescriptionUpload";
import ScheduleView from "./components/ScheduleView";
import { subscribeForPush } from "./utils/pushUtils";
import api from "./services/api";

function ErrorBoundary({ children }) {
  const [error, setError] = useState(null);

  if (error) {
    return <pre style={{ color: "red" }}>{error.message}</pre>;
  }

  try {
    return children;
  } catch (err) {
    setError(err);
    return null;
  }
}

export default function App() {
  const [medicines, setMedicines] = useState([]);

  useEffect(() => {
    fetchMedicines();
  }, []);

  async function fetchMedicines() {
    try {
      const res = await api.get("/api/medicines");
      setMedicines(res.data || []);
    } catch (err) {
      console.error("Failed to fetch medicines", err);
    }
  }

  async function onAdded() {
    await fetchMedicines();
  }

  async function onSubscribePush() {
    try {
      const { data } = await api.get("/api/vapidPublicKey");
      const vapidPublicKey = data.vapidPublicKey;
      const subscription = await subscribeForPush(vapidPublicKey);

      if (subscription) {
        await api.post("/api/subscribe", subscription);
        alert("Subscribed to push notifications!");
      }
    } catch (err) {
      console.error(err);
      alert("Push subscription failed. Check console.");
    }
  }

  return (
    <ErrorBoundary>
      <div
        style={{
          fontFamily: "Inter, sans-serif",
          maxWidth: 900,
          margin: "24px auto",
        }}
      >
        <header
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
          }}
        >
          <h1>MedMind</h1>
          <button onClick={onSubscribePush}>Enable Push Notifications</button>
        </header>

        <section
          style={{
            display: "grid",
            gridTemplateColumns: "1fr 1fr",
            gap: 20,
            marginTop: 20,
          }}
        >
          <div>
            <h2>Add Medicine</h2>
            <MedicineForm onAdded={onAdded} />
          </div>

          <div>
            <h2>Upload Prescription</h2>
            <PrescriptionUpload onAdded={onAdded} />
          </div>
        </section>

        <section style={{ marginTop: 32 }}>
          <h2>Your Schedule</h2>
          <ScheduleView medicines={medicines} />
        </section>
      </div>
    </ErrorBoundary>
  );
}
