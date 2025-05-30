import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import "./StationDetailsPage.css";
import personMarker from "./assets/personmarker.png";
import CONFIG from '../config';


const userLat = 40.6293194;
const userLng = -8.6544725;

export default function StationDetailsPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [station, setStation] = useState(null);



  useEffect(() => {
    const fetchStation = async () => {
      const token = localStorage.getItem("token");
      const res = await fetch(`${CONFIG.API_URL}stations/${id}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (res.ok) {
        const data = await res.json();
        setStation(data);
      } else {
        console.error("Failed to fetch station details.");
      }
    };

    fetchStation();
  }, [id]);

  useEffect(() => {
    if (station) {
      const map = L.map("station-details-map").setView([userLat, userLng], 11);

      const userIcon = L.icon({
      iconUrl: personMarker,
      iconSize: [32, 32],
      iconAnchor: [16, 32],
      popupAnchor: [0, -32]
      });

      L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        attribution: '&copy; OpenStreetMap contributors',
      }).addTo(map);

      // Station marker
      L.marker([station.latitude, station.longitude])
        .addTo(map)
        .bindPopup(`<strong>${station.name}</strong>`)
        .openPopup();

      // User marker
      L.marker([userLat, userLng], { icon: userIcon })
        .addTo(map)
        .bindPopup("<strong>You are here</strong>");


      return () => map.remove();
    }
  }, [station]);

  if (!station) return <div className="station-details-page">Loading...</div>;

  return (
    <div className="station-details-page">
      <button className="back-button" onClick={() => navigate(-1)}>← Back</button>

      <h1 className="details-title">{station.name}</h1>

      <div className="details-grid">
        <div className="details-box">Brand: {station.brand}</div>
        <div className="details-box">Address: {station.address}</div>
        <div className="details-box">Price: €{station.price}/kWh</div>
        <div className="details-box">Chargers: {station.numberOfChargers}</div>
        <div className="details-box">Hours: {station.openingHours} - {station.closingHours}</div>
      </div>

      <div className="map-container">
        <div id="station-details-map" className="leaflet-container" />
      </div>
    </div>
  );
}
