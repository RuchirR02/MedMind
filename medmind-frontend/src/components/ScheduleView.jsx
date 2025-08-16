import React from "react";

export default function ScheduleView({ medicines }) {
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
        {medicines.map((med, idx) => (
          <tr key={idx}>
            <td>{med.name}</td>
            <td>{med.time}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
