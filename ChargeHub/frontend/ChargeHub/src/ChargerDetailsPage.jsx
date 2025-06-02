// pages/ChargerDetailsPage.jsx
import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import CONFIG from "../config";
import BookingList from "./components/BookingList";

export default function ChargerDetailsPage() {
  const { id } = useParams(); // chargerId
  const navigate = useNavigate();
  const [charger, setCharger] = useState(null);
  const [selectedDate, setSelectedDate] = useState(new Date());

  useEffect(() => {
    const fetchCharger = async () => {
      const token = localStorage.getItem("token");

      try {
        const res = await fetch(`${CONFIG.API_URL}chargers/${id}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
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

  const today = new Date().toISOString().split("T")[0];

  if (!charger) return <div>Loading charger info...</div>;

  return (
    <div className="charger-details-page">
      <button onClick={() => navigate(-1)}>← Back</button>

      <h1>Charger #{charger.id}</h1>
      <div className="details-grid">
        <div>Type: {charger.type}</div>
        <div>Connector: {charger.connectorType}</div>
        <div>Power: {charger.power} kW</div>
        <div>Status: {charger.available ? "Available" : "Unavailable"}</div>
      </div>

      <div className="calendar-filter">
        <label>Select Date:</label>
        <input
          type="date"
          value={selectedDate.toISOString().split("T")[0]}
          min={today}
          onChange={handleDateChange}
        />
      </div>

      <BookingList chargerId={id} selectedDate={selectedDate} />

      <button
        className="book-button"
        onClick={() => navigate(`/book/${id}?date=${selectedDate.toISOString().split("T")[0]}`)}
      >
        Book
      </button>
    </div>
  );
}
