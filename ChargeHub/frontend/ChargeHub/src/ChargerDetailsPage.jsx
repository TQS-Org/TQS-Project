// pages/ChargerDetailsPage.jsx
import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import CONFIG from "../config";
import BookingList from "./components/BookingList";
import BookingForm from "./components/BookingForm";
import "./ChargerDetailsPage.css";

export default function ChargerDetailsPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [charger, setCharger] = useState(null);
  const [selectedDate, setSelectedDate] = useState(new Date());
  const [showBookingForm, setShowBookingForm] = useState(false);

  useEffect(() => {
    const fetchCharger = async () => {
      const token = localStorage.getItem("token");
      try {
        const res = await fetch(`${CONFIG.API_URL}charger/${id}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (res.ok) {
          const data = await res.json();
          setCharger(data);
        } else {
          console.error("Failed to fetch charger.");
        }
      } catch (err) {
        console.error(err);
      }
    };
    fetchCharger();
  }, [id]);

  const handleDateChange = (e) => {
    setSelectedDate(new Date(e.target.value));
  };

  const handleBookingSubmit = async (dto) => {
    const token = localStorage.getItem("token");
    try {
      const res = await fetch(`${CONFIG.API_URL}booking`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(dto),
      });
      if (res.ok) {
        return "Booking created successfully!";
      } else {
        const errorText = await res.text();
        throw new Error(errorText || "Booking failed.");
      }
    } catch (err) {
      throw err.message || "Unexpected error.";
    }
  };

  const today = new Date().toISOString().split("T")[0];
  if (!charger) {
    return (
      <div id="charger-details-page">
        <div className="loading">Loading charger info...</div>
      </div>
    );
  }

  return (
    <div id="charger-details-page">
      <button className="back-button" onClick={() => navigate(-1)}>
        ← Back
      </button>

      <div className="charger-card">
        <h1 className="charger-title">Charger #{charger.id}</h1>
        <div className="charger-info-grid">
          <div><strong>Type:</strong> {charger.type}</div>
          <div><strong>Connector:</strong> {charger.connectorType}</div>
          <div><strong>Power:</strong> {charger.power} kW</div>
          <div>
            <strong>Status:</strong>{" "}
            <span className={`status-badge ${charger.available ? "available" : "unavailable"}`}>
              {charger.available ? "Available" : "Unavailable"}
            </span>
          </div>
        </div>

        {/* Only show date picker, booking list and button if charger is available */}
        {charger.available && (
          <>
            <div className="date-picker-section">
              <label htmlFor="date" className="date-label">Select Date:</label>
              <input
                type="date"
                id="date"
                value={selectedDate.toISOString().split("T")[0]}
                min={today}
                onChange={handleDateChange}
                className="date-picker"
              />
            </div>

            <BookingList chargerId={id} selectedDate={selectedDate} />

            {showBookingForm && (
              <div className="modal-overlay" onClick={() => setShowBookingForm(false)}>
                <div
                  className="modal-content"
                  onClick={(e) => e.stopPropagation()}
                >
                  <BookingForm
                    station={charger.station}
                    chargerId={Number(id)}
                    onSubmit={handleBookingSubmit}
                  />
                  <button className="modal-close" onClick={() => setShowBookingForm(false)}>
                    ✖
                  </button>
                </div>
              </div>
            )}

            <button className="primary-button" onClick={() => setShowBookingForm(true)}>
              Book Charge
            </button>
          </>
        )}
      </div>
    </div>
  );
}
