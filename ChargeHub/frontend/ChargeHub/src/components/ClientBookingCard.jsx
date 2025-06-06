import { useState, useEffect } from "react";
import { useNavigate , Link} from "react-router-dom";
import "../css/BookingCard.css";
import CONFIG from "../../config";

export default function ClientBookingCard({ booking }) {
  const user = booking.user || { name: "Unknown", mail: "Unknown" };
  const navigate = useNavigate();
  const [hasChargingSession, setHasChargingSession] = useState(false);

  // Format ISO time
  const formatTime = (isoString) => {
    const date = new Date(isoString);
    return date.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
  };

  // Check if session exists when component mounts
  useEffect(() => {
    const checkChargingSession = async () => {
      const token = localStorage.getItem("token");
      try {
        const response = await fetch(`${CONFIG.API_URL}booking/${booking.id}/session`, {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (response.ok) {
          setHasChargingSession(true); // Just set the flag, don't navigate
        }
      } catch (error) {
        console.error("Failed to fetch session data:", error);
      }
    };

    checkChargingSession();
  }, [booking.id]);

  return (
    <div className="booking-card">
      <h3>Booking</h3>
      <p>Client: {user.name}</p>
      <p>Date: {booking.date}</p>
      <p>Start: {formatTime(booking.startTime)}</p>
      <p>End: {formatTime(booking.endTime)}</p>
      <p>Station: {booking.charger.station.name}</p>
      <p>Charger: {booking.charger.id}</p>
      <p>Token: {booking.token}</p>

      {/* View Charger Details button */}
      <Link to={`/chargers/${booking.charger.id}`} className="view-charger-button">
        Go to Charger
      </Link>

      {hasChargingSession && (
        <button onClick={() => navigate(`/booking/${booking.id}/status`)}>
          View Charging
        </button>
      )}
    </div>
  );
}

