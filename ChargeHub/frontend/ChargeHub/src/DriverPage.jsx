import { useEffect, useState, useCallback } from "react";
import "./DriverPage.css";
import CONFIG from "../config"; 

export default function DriverPage() {
  const [stations, setStations] = useState([]);
  const [filters, setFilters] = useState({
    district: "",
    maxPrice: "",
    chargerType: "",
    connectorType: "",
    available: ""
  });

  const fetchStations = useCallback(async () => {
    const params = new URLSearchParams();
    Object.entries(filters).forEach(([key, value]) => {
      if (value) params.append(key, value);
    });

    const token = localStorage.getItem("token");
    const endpoint = params.toString()
      ? `${CONFIG.API_URL}stations/search?${params.toString()}`
      : `${CONFIG.API_URL}stations`;

    const res = await fetch(endpoint, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      credentials: "include",
    });

    if (!res.ok) {
      const errText = await res.text();
      console.error("Failed to fetch stations:", res.status, errText);
      return;
    }

    try {
      const data = await res.json();
      setStations(data);
    } catch (err) {
      console.error("JSON parse error:", err);
    }
  }, [filters]);

  useEffect(() => {
    fetchStations();
  }, [fetchStations]);


  const handleFilterChange = (key, value) => {
    setFilters((prev) => ({ ...prev, [key]: value }));
  };

  return (
    <div className="driver-page">
      <div className="main-content">
        <aside className="sidebar">
          <h2>Filters</h2>

          <input
            type="text"
            placeholder="District"
            value={filters.district}
            onChange={(e) => handleFilterChange("district", e.target.value)}
          />

          <input
            type="number"
            placeholder="Max Price (€)"
            value={filters.maxPrice}
            onChange={(e) => handleFilterChange("maxPrice", e.target.value)}
          />

          <select
            value={filters.chargerType}
            onChange={(e) => handleFilterChange("chargerType", e.target.value)}
          >
            <option value="">All Charger Types</option>
            <option value="AC">AC</option>
            <option value="DC">DC</option>
            <option value="FAST">FAST</option>
          </select>

          <select
            value={filters.connectorType}
            onChange={(e) =>
              handleFilterChange("connectorType", e.target.value)
            }
          >
            <option value="">All Connector Types</option>
            <option value="CCS">CCS</option>
            <option value="CHAdeMO">CHAdeMO</option>
            <option value="Type2">Type2</option>
          </select>

          <label>
            <input
              type="checkbox"
              checked={filters.available === "true"}
              onChange={(e) =>
                handleFilterChange("available", e.target.checked ? "true" : "")
              }
            />
            Available only
          </label>

          <button onClick={fetchStations}>Search</button>
        </aside>

        <section className="card-section">
          <div className="card-section-title">Stations</div> {/* <-- outside grid */}

          <div className="station-list">
            {stations.map((station) => (
              <div className="station-card" key={station.id}>
                <h2>{station.name}</h2>
                <p><strong>Brand:</strong> {station.brand}</p>
                <p><strong>District:</strong> {station.address}</p>
                <p><strong>Chargers:</strong> {station.numberOfChargers}</p>
                <p><strong>Price:</strong> €{station.price.toFixed(2)}/kWh</p>
                <p><strong>Hours:</strong> {station.openingHours} - {station.closingHours}</p>
              </div>
            ))}
          </div>
        </section>
      </div>
    </div>
  );
}
