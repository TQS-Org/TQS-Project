import { useEffect, useState } from "react";
import CONFIG from "../../config";

export default function AddChargerModal({ station, onClose }) {
  const [chargers, setChargers] = useState([]);
  const [errorMsg, setErrorMsg] = useState("");

  // Fetch chargers when station changes
  useEffect(() => {
    if (!station) return;

    const token = localStorage.getItem("token");
    fetch(`${CONFIG.API_URL}stations/${station.id}/chargers`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => {
        if (!res.ok) throw new Error("Failed to fetch chargers");
        return res.json();
      })
      .then(setChargers)
      .catch((err) => {
        console.error(err);
        setChargers([]); // fallback
      });
  }, [station]);

  const handleAddCharger = (e) => {
    e.preventDefault();
    const form = e.target;

    const dto = {
      type: form.type.value,
      connectorType: form.connectorType.value,
      power: parseFloat(form.power.value),
      available: true,
    };

    const token = localStorage.getItem("token");
    fetch(`${CONFIG.API_URL}charger/${station.id}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(dto),
    })
      .then((res) => {
        if (!res.ok)
          return res.text().then((text) => {
            throw new Error(text);
          });
        return res.json();
      })
      .then((newCharger) => {
        // Update chargers list
        setChargers((prev) => [...prev, newCharger]);
        form.reset();
        setErrorMsg("");
      })
      .catch((err) => {
        setErrorMsg(err.message || "Something went wrong");
      });
  };

  if (!station) return null;

  return (
    <div className="modal-overlay">
      <div className="modal">
        <h2>Chargers for {station.name}</h2>

        <div style={{ maxHeight: "200px", overflowY: "auto" }}>
          {chargers.length > 0 ? (
            <ul>
              {chargers.map((ch) => (
                <li key={ch.id}>
                  <strong>{ch.type}</strong> - {ch.connectorType} - {ch.power}kW -{" "}
                  {ch.available ? "Available" : "Not available"}
                </li>
              ))}
            </ul>
          ) : (
            <p>No chargers yet.</p>
          )}
        </div>

        <h3>Add a new charger</h3>
        <form onSubmit={handleAddCharger}>
          <input name="type" placeholder="Type (e.g. AC/DC)" required />
          <input
            name="connectorType"
            placeholder="Connector Type (e.g. CCS)"
            required
          />
          <input
            name="power"
            placeholder="Power (kW)"
            type="number"
            step="0.1"
            min="0"
            required
          />

          {errorMsg && <p className="error-msg">{errorMsg}</p>}

          <div className="modal-buttons">
            <button type="submit">Add Charger</button>
            <button
              type="button"
              className="cancel-button"
              onClick={onClose}
            >
              Close
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
