import { useEffect, useState, useCallback } from "react";
import L from "leaflet";
import { Link } from "react-router-dom";
import "leaflet/dist/leaflet.css";
import "./DriverPage.css";
import CONFIG from "../config"; 
import personMarker from "./assets/personmarker.png";

import markerIcon2x from 'leaflet/dist/images/marker-icon-2x.png';
import markerIcon from 'leaflet/dist/images/marker-icon.png';
import markerShadow from 'leaflet/dist/images/marker-shadow.png';

delete L.Icon.Default.prototype._getIconUrl;

L.Icon.Default.mergeOptions({
  iconRetinaUrl: markerIcon2x,
  iconUrl: markerIcon,
  shadowUrl: markerShadow
});


function getDistance(lat1, lon1, lat2, lon2) {
  function toRad(x) {
    return x * Math.PI / 180;
  }

  const R = 6371; // km
  const dLat = toRad(lat2 - lat1);
  const dLon = toRad(lon2 - lon1);
  const a =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(toRad(lat1)) *
      Math.cos(toRad(lat2)) *
      Math.sin(dLon / 2) *
      Math.sin(dLon / 2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return R * c;
}


export default function DriverPage() {
  const [stations, setStations] = useState([]);
  const [filters, setFilters] = useState({
    district: "",
    maxPrice: "",
    chargerType: "",
    connectorType: "",
    available: ""
  });

  const sortByDistance = () => {
  const userLat = 40.6293194;
  const userLng = -8.6544725;

  const sorted = [...stations].sort((a, b) => {
    const distA = getDistance(userLat, userLng, a.latitude, a.longitude);
    const distB = getDistance(userLat, userLng, b.latitude, b.longitude);
    return distA - distB;
  });

  setStations(sorted);
  };

  const [showMap, setShowMap] = useState(false);


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

  // Setup the map after modal is shown
    useEffect(() => {
    if (showMap && stations.length) {
      const map = L.map("station-map").setView([39.5, -8], 7); // Initial center


      L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        attribution: '&copy; OpenStreetMap contributors',
      }).addTo(map);

      // Station markers
      stations.forEach(station => {
        if (station.latitude && station.longitude) {
          L.marker([station.latitude, station.longitude])
            .addTo(map)
            .bindPopup(`
              <div style="font-size: 14px; line-height: 1.4">
                <strong>${station.name}</strong><br/>
                <strong>Brand:</strong> ${station.brand}<br/>
                <strong>Address:</strong> ${station.address}<br/>
                <strong>Chargers:</strong> ${station.numberOfChargers}<br/>
                <strong>Price:</strong> €${station.price.toFixed(2)}/kWh<br/>
                <strong>Hours:</strong> ${station.openingHours} - ${station.closingHours}
              </div>
            `);
        }
      });

      // Hardcoded user location (Aveiro)
      const userLat = 40.6293194;
      const userLng = -8.6544725;

      const userIcon = L.icon({
      iconUrl: personMarker,
      iconSize: [32, 32],
      iconAnchor: [16, 32],
      popupAnchor: [0, -32]
      });


      L.marker([userLat, userLng], { icon: userIcon }).addTo(map).bindPopup("<strong>You are here</strong>").openPopup();

      map.setView([userLat, userLng], 11); // Center on user

      return () => map.remove();
    }
  }, [showMap, stations]);


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
            onChange={(e) => handleFilterChange("connectorType", e.target.value)}
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
          <button onClick={sortByDistance}>See Nearby Stations</button>
          <button onClick={() => setShowMap(true)}>Map View</button>
        </aside>

        <section className="card-section">
          <div className="card-section-title">Stations</div>

          <div className="station-list">
            {stations.map((station) => (
              <div className="station-card" key={station.id}>
                <Link to={`/stations/${station.id}`} className="station-card" key={station.id}>
                  <h2>{station.name}</h2>
                  <p><strong>Brand:</strong> {station.brand}</p>
                  <p><strong>District:</strong> {station.address}</p>
                  <p><strong>Chargers:</strong> {station.numberOfChargers}</p>
                  <p><strong>Price:</strong> €{station.price.toFixed(2)}/kWh</p>
                  <p><strong>Hours:</strong> {station.openingHours} - {station.closingHours}</p>
                  
                </Link>
              </div>
            ))}
          </div>
        </section>
      </div>

      {/* Modal */}
      {showMap && (
        <div className="modal-overlay" onClick={() => setShowMap(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div id="station-map" className="leaflet-container" />
          </div>
        </div>
      )}
    </div>
  );
}
