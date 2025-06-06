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
  const [showSessionModal, setShowSessionModal] = useState(false);
  const [chargeToken, setChargeToken] = useState("");
  const [sessionMessage, setSessionMessage] = useState(null);
  const [sessionError, setSessionError] = useState(null);


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

    const handleStartSession = async () => {
      const token = localStorage.getItem("token");
      if (!chargeToken) {
        setSessionError("Please enter a valid token.");
        return;
      }

      try {
        // Step 1: Start the charging session
        const res = await fetch(`${CONFIG.API_URL}charger/${id}/session`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({ chargeToken }),
        });

        const text = await res.text();
        if (res.ok) {
          setSessionMessage(text);
          setSessionError(null);

          // Step 2: Call /api/payment/create-checkout-session
          const paymentRes = await fetch(
            `${CONFIG.API_URL}payment/create-checkout-session?bookingToken=${chargeToken}`,
            {
              method: "POST",
              headers: {
                Authorization: `Bearer ${token}`,
              },
            }
          );

          if (paymentRes.ok) {
            const paymentData = await paymentRes.json();
            if (paymentData.url) {
              // Redirect to Stripe Checkout page
              window.location.href = paymentData.url;
            } else {
              alert("No payment URL returned.");
            }
          } else {
            const errorText = await paymentRes.text();
            alert("Payment session creation failed: " + errorText);
          }

        } else {
          setSessionMessage(null);
          setSessionError(text);
        }
      } catch (err) {
        setSessionError("An error occurred while starting the session.");
        console.error(err);
      }
    };




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
                  selectedDate={selectedDate} // <== add this line
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

            <button className="primary-button mt-2" onClick={() => setShowSessionModal(true)}>
              Start Charging Session
            </button>

            {showSessionModal && (
              <div className="modal-overlay" onClick={() => setShowSessionModal(false)}>
                <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                  <h2 className="text-lg font-semibold mb-2">Enter Charge Token</h2>
                  <input
                    type="text"
                    className="input-token"
                    value={chargeToken}
                    maxLength={6}
                    onChange={(e) => setChargeToken(e.target.value)}
                    placeholder="Enter token"
                  />
                  <button className="primary-button mt-2" onClick={handleStartSession}>
                    Submit Token
                  </button>
                  <button className="modal-close" onClick={() => setShowSessionModal(false)}>
                    ✖
                  </button>
            
                  {sessionMessage && <p className="text-green-600 mt-2">{sessionMessage}</p>}
                  {sessionError && <p className="text-red-600 mt-2">{sessionError}</p>}
                </div>
              </div>
            )}
          </>
          
        )}
      </div>
    </div>
  );
}
