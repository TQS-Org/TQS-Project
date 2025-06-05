import { useEffect, useState } from "react";
import "./css/AdminStations.css";
import CONFIG from "../config";
import AddChargerModal from "./components/AddChargerModal";

export default function AdminStations() {
  const [stations, setStations] = useState([]);
  const [search, setSearch] = useState("");
  const [sort, setSort] = useState("az");
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");
  const [showAddChargerModal, setShowAddChargerModal] = useState(false);
  const [selectedStation, setSelectedStation] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem("token");
    fetch(`${CONFIG.API_URL}stations`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => res.json())
      .then(setStations);
  }, []);

  const filteredAndSorted = [...stations]
    .filter((st) => st.name.toLowerCase().includes(search.toLowerCase()))
    .sort((a, b) =>
      sort === "az"
        ? a.name.localeCompare(b.name)
        : b.name.localeCompare(a.name)
    );

  return (
    <div className="operator-section">
      <div className="operator-controls">
        <input
          type="text"
          placeholder="Search by name..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <select value={sort} onChange={(e) => setSort(e.target.value)}>
          <option value="az">Name: A → Z</option>
          <option value="za">Name: Z → A</option>
        </select>
      </div>

      <div className="operator-list">
        <div
          className="operator-card create-card"
          onClick={() => setShowCreateModal(true)}
        >
          <div className="plus-icon">+</div>
          <p>Create Station</p>
        </div>

        {filteredAndSorted.map((st) => (
          <div
            key={st.id}
            className="operator-card"
            onClick={() => {
              setSelectedStation(st);
              setShowAddChargerModal(true);
            }}
          >
            <h3>{st.name}</h3>
            <p><strong>Address:</strong> {st.address}</p>
            <p><strong>Brand:</strong> {st.brand}</p>
            <p><strong>Chargers:</strong> {st.numberOfChargers}</p>
            <p><strong>Price:</strong> €{st.price}</p>
          </div>
        ))}
      </div>

      {showCreateModal && (
        <div className="modal-overlay">
          <div className="modal">
            <h2>Create Station</h2>
            <form
              onSubmit={(e) => {
                e.preventDefault();
                const form = e.target;

                const openingHours = form.openingHours.value;
                const closingHours = form.closingHours.value;
                const timeRegex = /^([01]\d|2[0-3]):([0-5]\d)$/;
                if (!timeRegex.test(openingHours) || !timeRegex.test(closingHours)) {
                  setErrorMsg("Hours must be in the format HH:MM (24h)");
                  return;
                }

                setErrorMsg("");

                const dto = {
                  name: form.name.value,
                  brand: form.brand.value,
                  latitude: parseFloat(form.latitude.value),
                  longitude: parseFloat(form.longitude.value),
                  address: form.address.value,
                  numberOfChargers: parseInt(form.numberOfChargers.value, 10),
                  openingHours: form.openingHours.value,
                  closingHours: form.closingHours.value,
                  price: parseFloat(form.price.value),
                };

                const token = localStorage.getItem("token");
                fetch(`${CONFIG.API_URL}stations`, {
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
                  .then(() => {
                    setShowCreateModal(false);
                    setErrorMsg("");
                    setShowSuccessModal(true);

                    // Re-fetch updated list
                    fetch(`${CONFIG.API_URL}stations`, {
                      headers: { Authorization: `Bearer ${token}` },
                    })
                      .then((res) => res.json())
                      .then(setStations);
                  })
                  .catch((err) => {
                    setErrorMsg(err.message || "Something went wrong");
                  });
              }}
            >
              <input name="name" placeholder="Name" required />
              <input name="brand" placeholder="Brand" required />
              <input
                name="latitude"
                placeholder="Latitude"
                type="number"
                step="0.000001"
                required
              />
              <input
                name="longitude"
                placeholder="Longitude"
                type="number"
                step="0.000001"
                required
              />
              <input name="address" placeholder="Address" required />
              <input
                name="numberOfChargers"
                placeholder="Number of Chargers"
                type="number"
                min="1"
                required
              />
              <input
                name="openingHours"
                placeholder="Opening Hours (HH:MM)"
                pattern="^([01]\d|2[0-3]):([0-5]\d)$"
                title="Enter hours in HH:MM (24h)"
                required
              />
              <input
                name="closingHours"
                placeholder="Closing Hours (HH:MM)"
                pattern="^([01]\d|2[0-3]):([0-5]\d)$"
                title="Enter hours in HH:MM (24h)"
                required
                onInput={(e) => {
                  if (e.target.value === "24:00") {
                    e.target.setCustomValidity("Closing hour cannot be 24:00. Use 23:59 instead.");
                  } else {
                    e.target.setCustomValidity("");
                  }
                }}
              />

              <input
                name="price"
                placeholder="Price"
                type="number"
                step="0.01"
                min="0"
                required
              />

              {errorMsg && <p className="error-msg">{errorMsg}</p>}

              <div className="modal-buttons">
                <button type="submit">Create</button>
                <button
                  type="button"
                  className="cancel-button"
                  onClick={() => setShowCreateModal(false)}
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {showSuccessModal && (
        <div className="modal-overlay">
          <div className="modal success-animation">
            <svg width="200" height="200">
              <circle
                fill="none"
                stroke="#68E534"
                strokeWidth="14"
                cx="100"
                cy="100"
                r="90"
                strokeLinecap="round"
                transform="rotate(-90 100 100)"
                className="circle"
              />
              <polyline
                fill="none"
                stroke="#68E534"
                points="50,110 85,140 145,65"
                strokeWidth="16"
                strokeLinecap="round"
                strokeLinejoin="round"
                className="tick"
              />
            </svg>
            <h3>Station Created!</h3>
            <button
              type="button"
              className="close-button"
              onClick={() => setShowSuccessModal(false)}
            >
              Close
            </button>
          </div>
        </div>
      )}

      {showAddChargerModal && (
        <AddChargerModal
          station={selectedStation}
          onClose={() => setShowAddChargerModal(false)}
          onChargerAdded={() => {
            const token = localStorage.getItem("token");
            fetch(`${CONFIG.API_URL}stations`, {
              headers: { Authorization: `Bearer ${token}` },
            })
              .then((res) => res.json())
              .then(setStations);
          }}
        />
      )}

    </div>
  );
}
