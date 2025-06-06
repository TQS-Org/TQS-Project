import { useEffect, useState } from "react";
import CONFIG from "../config";
import "./OperatorPage.css";

export default function OperatorStationPage() {
  const [station, setStation] = useState(null);
  const [chargers, setChargers] = useState([]);
  const [editMode, setEditMode] = useState(false);
  const [formData, setFormData] = useState({});
  const [message, setMessage] = useState(null);

  const token = localStorage.getItem("token");

  useEffect(() => {
    const fetchStation = async () => {
      try {
        const res = await fetch(`${CONFIG.API_URL}staff/station`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (res.ok) {
          const data = await res.json();
          setStation(data);
          setFormData(data); // initialize edit form data
          fetchChargers(data.id);
        } else {
          console.error("Failed to load station.");
        }
      } catch (err) {
        console.error(err);
      }
    };

    const fetchChargers = async (stationId) => {
      try {
        const res = await fetch(`${CONFIG.API_URL}stations/${stationId}/chargers`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (res.ok) {
          const data = await res.json();
          setChargers(data);
        } else {
          console.error("Failed to load chargers.");
        }
      } catch (err) {
        console.error(err);
      }
    };

    fetchStation();
  }, [token]);

  const handleStationChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleStationUpdate = async () => {
    try {
      const res = await fetch(`${CONFIG.API_URL}stations/${station.id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(formData),
      });
      if (res.ok) {
        setMessage("Station updated successfully!");
        setEditMode(false);
        setStation(formData); // update view
      } else {
        setMessage("Failed to update station.");
      }
    } catch (err) {
      console.error(err);
      setMessage("Error occurred while updating station.");
    }
  };

  const handleChargerUpdate = async (chargerId, updatedFields) => {
    try {
      const res = await fetch(`${CONFIG.API_URL}charger/${chargerId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(updatedFields),
      });
      if (res.ok) {
        setMessage("Charger updated successfully!");
        setChargers((prev) =>
          prev.map((c) => (c.id === chargerId ? { ...c, ...updatedFields } : c))
        );
      } else {
        setMessage("Failed to update charger.");
      }
    } catch (err) {
      console.error(err);
      setMessage("Error occurred while updating charger.");
    }
  };


  if (!station) return <div>Loading your station info...</div>;

  return (
    <div className="operator-station-page">
      <h1>My Station</h1>

      {editMode ? (
        <div className="station-edit-form">
          <input
            type="text"
            name="name"
            value={formData.name || ""}
            onChange={handleStationChange}
            placeholder="Station name"
          />
          <input
            type="text"
            name="brand"
            value={formData.brand || ""}
            onChange={handleStationChange}
            placeholder="Brand"
          />
          <input
            type="text"
            name="address"
            value={formData.address || ""}
            onChange={handleStationChange}
            placeholder="Address"
          />
          <input
            type="number"
            name="latitude"
            value={formData.latitude || ""}
            onChange={handleStationChange}
            placeholder="Latitude"
          />
          <input
            type="number"
            name="longitude"
            value={formData.longitude || ""}
            onChange={handleStationChange}
            placeholder="Longitude"
          />
          <input
            type="number"
            name="numberOfChargers"
            value={formData.numberOfChargers || ""}
            onChange={handleStationChange}
            placeholder="Number of chargers"
          />
          <input
            type="text"
            name="openingHours"
            value={formData.openingHours || ""}
            onChange={handleStationChange}
            placeholder="Opening hours"
          />
          <input
            type="text"
            name="closingHours"
            value={formData.closingHours || ""}
            onChange={handleStationChange}
            placeholder="Closing hours"
          />
          <input
            type="number"
            name="price"
            value={formData.price || ""}
            onChange={handleStationChange}
            placeholder="Price"
          />
          <button onClick={handleStationUpdate}>Save</button>
          <button onClick={() => setEditMode(false)}>Cancel</button>
        </div>
      ) : (
        <div className="station-info">
          <p><strong>Name:</strong> {station.name}</p>
          <p><strong>Brand:</strong> {station.brand}</p>
          <p><strong>Address:</strong> {station.address}</p>
          <p><strong>Latitude:</strong> {station.latitude}</p>
          <p><strong>Longitude:</strong> {station.longitude}</p>
          <p><strong>Number of Chargers:</strong> {station.numberOfChargers}</p>
          <p><strong>Opening Hours:</strong> {station.openingHours}</p>
          <p><strong>Closing Hours:</strong> {station.closingHours}</p>
          <p><strong>Price (€/min):</strong> {station.price}</p>
          <button onClick={() => setEditMode(true)}>Edit Station</button>
        </div>
      )}

      <h2>Chargers</h2>
      {chargers.length === 0 ? (
        <p>No chargers found.</p>
      ) : (
        <div className="chargers-list">
          {chargers.map((charger) => (
            <ChargerCard
              key={charger.id}
              charger={charger}
              onUpdate={handleChargerUpdate}
            />
          ))}
        </div>
      )}

      {message && <p className="message">{message}</p>}
    </div>
  );
}

function ChargerCard({ charger, onUpdate }) {
  const [editMode, setEditMode] = useState(false);
  const [formData, setFormData] = useState(charger);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleUpdate = () => {
    onUpdate(charger.id, formData);
    setEditMode(false);
  };

  return (
    <div className="charger-card">
      {editMode ? (
        <>
          <input
            type="text"
            name="type"
            value={formData.type}
            onChange={handleChange}
            placeholder="Type"
          />
          <input
            type="text"
            name="connectorType"
            value={formData.connectorType}
            onChange={handleChange}
            placeholder="Connector"
          />
          <input
            type="number"
            name="power"
            value={formData.power}
            onChange={handleChange}
            placeholder="Power"
          />
          <select
            name="available"
            value={formData.available ? "true" : "false"}
            onChange={(e) => setFormData({ ...formData, available: e.target.value === "true" })}
          >
            <option value="true">Available</option>
            <option value="false">Unavailable</option>
          </select>
          <button onClick={handleUpdate}>Save</button>
          <button onClick={() => setEditMode(false)}>Cancel</button>
        </>
      ) : (
        <>
          <p><strong>Type:</strong> {charger.type}</p>
          <p><strong>Connector:</strong> {charger.connectorType}</p>
          <p><strong>Power:</strong> {charger.power} kW</p>
          <p><strong>Available:</strong> {charger.available ? "Yes" : "No"}</p>
          <button onClick={() => setEditMode(true)}>Edit</button>
        </>
      )}
    </div>
  );
}
