import { useEffect, useState } from "react";
import "./AdminOperators.css"; // Reuse existing styles
import CONFIG from "../config";

export default function AdminStations() {
  const [stations, setStations] = useState([]);
  const [search, setSearch] = useState("");
  const [sort, setSort] = useState("az");
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");

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
          <div key={st.id} className="operator-card">
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

                const dto = {
                  name: form.name.value,
                  brand: form.brand.value,
                  address: form.address.value,
                  latitude: parseFloat(form.latitude.value),
                  longitude: parseFloat(form.longitude.value),
                  numberOfChargers: parseInt(form.numberOfChargers.value),
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
                    if (!res.ok) return res.text().then((t) => { throw new Error(t); });
                    return res.json();
                  })
                  .then(() => {
                    setShowCreateModal(false);
                    setErrorMsg("");
                    setShowSuccessModal(true);

                    fetch(`${CONFIG.API_URL}stations`, {
                      headers: { Authorization: `Bearer ${token}` },
                    })
                      .then((res) => res.json())
                      .then(setStations);
                  })
                  .catch((err) => setErrorMsg(err.message || "Error"));
              }}
            >
              <input name="name" placeholder="Name" required />
              <input name="brand" placeholder="Brand" required />
              <input name="address" placeholder="Address" required />
              <input name="latitude" type="number" step="any" placeholder="Latitude" required />
              <input name="longitude" type="number" step="any" placeholder="Longitude" required />
              <input name="numberOfChargers" type="number" placeholder="Number of Chargers" required />
              <input name="openingHours" placeholder="Opening Hours" required />
              <input name="closingHours" placeholder="Closing Hours" required />
              <input name="price" type="number" step="0.01" placeholder="Price (€)" required />

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
    </div>
  );
}
