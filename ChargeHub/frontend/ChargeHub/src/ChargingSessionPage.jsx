import React, { useEffect, useState, useRef } from "react";
import { useParams } from "react-router-dom";
import "./ChargingStatus.css";

export default function ChargingStatusPage() {
  const { id } = useParams();
  const [session, setSession] = useState(null);
  const [booking, setBooking] = useState(null);
  const [progress, setProgress] = useState(0);
  const [energy, setEnergy] = useState(0);
  const [chargingEnded, setChargingEnded] = useState(false);
  const intervalRef = useRef(null);

  const token = localStorage.getItem("token");

  useEffect(() => {
    const fetchData = async () => {
      try {
        const bookingRes = await fetch(`/api/booking/${id}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!bookingRes.ok) throw new Error("Failed to fetch booking");
        const bookingData = await bookingRes.json();
        setBooking(bookingData);

        const sessionRes = await fetch(`/api/booking/${id}/session`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!sessionRes.ok) throw new Error("Failed to fetch session");
        const sessionData = await sessionRes.json();
        setSession(sessionData);
      } catch (error) {
        console.error("Error fetching data:", error);
        alert("Failed to load booking or session.");
      }
    };

    fetchData();
  }, [id, token]);

  useEffect(() => {
  if (!session || !booking) return;

  const startTime = new Date(session.startTime);
  const endTime = new Date(session.endTime);
  const power = booking.charger.power;

  const now = new Date();
  const isSessionConcluded = session.sessionStatus === "CONCLUDED" || now >= endTime;

  if (isSessionConcluded) {
    setChargingEnded(true);
    setProgress(100);
    setEnergy(session.energyConsumed || 0);
    return; // Don't start interval
  }

  const totalDurationSec = (endTime - startTime) / 1000;

  intervalRef.current = setInterval(() => {
    const now = new Date();
    const elapsedSec = (now - startTime) / 1000;
    const currentProgress = Math.min((elapsedSec / totalDurationSec) * 100, 100);

    setProgress(currentProgress);

    const energyUsed = (elapsedSec / 3600) * power;
    setEnergy(energyUsed);

    if (currentProgress >= 100) {
      handleStopCharging();
    }
  }, 1000);

  return () => clearInterval(intervalRef.current);
}, [session, booking]);


  const handleStopCharging = async () => {
    if (chargingEnded || !session || !booking) return;

    clearInterval(intervalRef.current);
    setChargingEnded(true);

    const now = new Date().toISOString();

    try {
      const res = await fetch(
        `/api/charger/${booking.charger.id}/session/${session.id}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            energyConsumed: energy,
            endTime: now,
          }),
        }
      );

      if (!res.ok) throw new Error("Failed to stop session");
      alert("Charging session successfully concluded.");
    } catch (err) {
      console.error("Error stopping session:", err);
      alert("Failed to stop charging session.");
    }
  };

  if (!session || !booking) return <p style={{ color: "white" }}>Loading...</p>;

  return (
    <div className="battery-container">
      <h2>{chargingEnded ? "Charging Session Completed" : "Charging in Progress"}</h2>
      <div className="battery">
        <div
          className="battery-fill"
          style={{ width: `${progress}%` }}
        ></div>
      </div>
      <p>{Math.floor(progress)}%</p>
      <p>Energy Consumed: {(chargingEnded ? session.energyConsumed : energy).toFixed(2)} kWh</p>
<p>Total Cost: {(chargingEnded ? session.price : energy * booking.charger.station.price).toFixed(2)} €</p>
      {!chargingEnded && (
        <button onClick={handleStopCharging}>Stop Charging</button>
      )}
      {chargingEnded && <p>Charging session finished.</p>}
    </div>
  );
}
