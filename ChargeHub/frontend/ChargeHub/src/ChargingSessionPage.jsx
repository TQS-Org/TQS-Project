import React, { useEffect, useState, useRef, useCallback } from "react";
import { useParams } from "react-router-dom";
import "./ChargingStatus.css";
import CONFIG from "../config";

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
        const bookingRes = await fetch(`${CONFIG.API_URL}booking/${id}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!bookingRes.ok) throw new Error("Failed to fetch booking");
        const bookingData = await bookingRes.json();
        setBooking(bookingData);

        const sessionRes = await fetch(`${CONFIG.API_URL}booking/${id}/session`, {
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

  const handleStopCharging = useCallback(
    async (isAutoStop = false, finalEnergy = null) => {
      if ((!isAutoStop && chargingEnded) || !session || !booking) return;

      clearInterval(intervalRef.current);

      const nowISO = new Date().toISOString();
      const energyToSend = finalEnergy ?? energy;

      try {
        const res = await fetch(
          `${CONFIG.API_URL}charger/${booking.charger.id}/session/${session.id}`,
          {
            method: "PUT",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify({
              energyConsumed: energyToSend,
              endTime: nowISO,
            }),
          }
        );

        if (!res.ok) throw new Error("Failed to stop session");

        const updatedSessionRes = await fetch(`${CONFIG.API_URL}booking/${id}/session`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!updatedSessionRes.ok)
          throw new Error("Failed to fetch updated session");
        const updatedSession = await updatedSessionRes.json();
        setSession(updatedSession);
        setEnergy(energyToSend);
        setChargingEnded(true);

        if (!isAutoStop) alert("Charging session successfully concluded.");
      } catch (err) {
        console.error("Error stopping session:", err);
        if (!isAutoStop) alert("Failed to stop charging session.");
      }
    },
    [chargingEnded, session, booking, energy, token, id]
  );

  useEffect(() => {
    if (!session || !booking) return;

    const startTime = new Date(session.startTime);
    const endTime = new Date(session.endTime);
    const power = booking.charger.power;

    const isSessionConcluded = session.sessionStatus === "CONCLUDED";

    if (isSessionConcluded) {
      setChargingEnded(true);
      setProgress(100);
      setEnergy(session.energyConsumed || 0);
      return;
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
        clearInterval(intervalRef.current);
        const finalEnergy = (elapsedSec / 3600) * power;
        handleStopCharging(true, finalEnergy);
      }
    }, 1000);

    return () => clearInterval(intervalRef.current);
  }, [session, booking, handleStopCharging]);

  if (!session || !booking) return <p style={{ color: "white" }}>Loading...</p>;

  return (
    <div className="charging-status-container">
      <h2>{chargingEnded ? "Charging Session Completed" : "Charging in Progress"}</h2>
      <div className="battery">
        <div className="battery-fill" style={{ width: `${progress}%` }} />
      </div>
      <p>{Math.floor(progress)}%</p>
      <p>
        Energy Consumed:{" "}
        {(chargingEnded ? session.energyConsumed : energy).toFixed(2)} kWh
      </p>
      {!chargingEnded ? (
        <button onClick={handleStopCharging}>Stop Charging</button>
      ) : (
        <p>Charging session finished.</p>
      )}
    </div>
  );
}
