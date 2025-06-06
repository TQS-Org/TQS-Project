import React from "react";
import { useNavigate } from "react-router-dom";

export default function CancelPage() {
  const navigate = useNavigate();

  return (
    <div className="cancel-page" style={{ textAlign: "center", marginTop: "100px" }}>
      <h1 style={{ fontSize: "2rem", color: "#e53e3e" }}>Payment Cancelled</h1>
      <p style={{ marginTop: "20px" }}>
        Oops! It looks like you cancelled your payment.
      </p>
      <button
        onClick={() => navigate("/driver")}
        style={{
          marginTop: "40px",
          padding: "10px 20px",
          backgroundColor: "#4299e1",
          color: "#fff",
          border: "none",
          borderRadius: "4px",
          cursor: "pointer",
          fontSize: "1rem"
        }}
      >
        Go back to Home
      </button>
    </div>
  );
}
