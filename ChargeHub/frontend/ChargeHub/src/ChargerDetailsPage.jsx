// pages/ChargerDetailsPage.jsx
import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import CONFIG from "../config";
import BookingList from "./components/BookingList";
import BookingForm from "./components/BookingForm";

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
  if (!charger) return <div>Loading charger info...</div>;

  return (
    <div className="charger-details-page p-4 max-w-2xl mx-auto">
      <button onClick={() => navigate(-1)} className="mb-2 text-blue-500">
        ← Back
      </button>

      <h1 className="text-xl font-bold mb-2">Charger #{charger.id}</h1>
      <div className="details-grid grid grid-cols-2 gap-2 mb-4">
        <div>Type: {charger.type}</div>
        <div>Connector: {charger.connectorType}</div>
        <div>Power: {charger.power} kW</div>
        <div>Status: {charger.available ? "Available" : "Unavailable"}</div>
      </div>

      <div className="calendar-filter mb-4">
        <label className="block mb-1">Select Date:</label>
        <input
          type="date"
          value={selectedDate.toISOString().split("T")[0]}
          min={today}
          onChange={handleDateChange}
          className="border rounded p-1"
        />
      </div>

      <BookingList chargerId={id} selectedDate={selectedDate} />

      {!showBookingForm ? (
        <button
          className="mt-4 px-4 py-2 bg-green-600 text-white rounded"
          onClick={() => setShowBookingForm(true)}
        >
          Book Charge
        </button>
      ) : (
        <div className="mt-4">
          <BookingForm
            station={charger.station}
            chargerId={Number(id)}
            onSubmit={handleBookingSubmit}
          />
          <button
            className="mt-2 text-sm text-red-500"
            onClick={() => setShowBookingForm(false)}
          >
            Cancel
          </button>
        </div>
      )}
    </div>
  );
}
