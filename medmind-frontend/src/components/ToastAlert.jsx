import React, { useEffect } from "react";

const ToastAlert = ({ message, onClose, duration = 3000 }) => {
  useEffect(() => {
    const timer = setTimeout(() => {
      onClose();
    }, duration);
    return () => clearTimeout(timer);
  }, [onClose, duration]);

  return (
    <div style={{
      position: "fixed",
      bottom: "20px",
      right: "20px",
      backgroundColor: "#323232",
      color: "#fff",
      padding: "12px 20px",
      borderRadius: "8px",
      boxShadow: "0px 2px 8px rgba(0,0,0,0.3)",
      zIndex: 1000
    }}>
      {message}
    </div>
  );
};

export default ToastAlert;
