import "../css/ChargerCard.css";
import { useNavigate } from "react-router-dom";

export default function ChargerCard({ charger }) {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate(`/chargers/${charger.id}`);
  };

  return (
    <div className="charger-card" onClick={handleClick} style={{ cursor: "pointer" }}>
      <h3>Charger #{charger.id}</h3>
      <p>Type: {charger.type}</p>
      <p>Connector: {charger.connectorType}</p>
      <p>Power: {charger.power} kW</p>
      <p>Status: {charger.available ? "Available" : "Unavailable"}</p>
    </div>
  );
}
