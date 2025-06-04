import { useEffect, useState } from "react";
import ChargerCard from "./ChargerCard";
import CONFIG from "../../config";

export default function ChargerList({ stationId }) {
  const [chargers, setChargers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchChargers = async () => {
      const token = localStorage.getItem("token");

      try {
        const res = await fetch(`${CONFIG.API_URL}stations/${stationId}/chargers`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (res.ok) {
          const data = await res.json();
          setChargers(data);
        } else {
          console.error("Failed to fetch chargers.");
        }
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchChargers();
  }, [stationId]);

  if (loading) return <div>Loading chargers...</div>;

  if (chargers.length === 0) return <div>No chargers found.</div>;

  return (
    <div className="charger-list">
      <h2>Chargers at this station</h2>
      <div className="charger-list-grid">
        {chargers.map((charger) => (
          <ChargerCard key={charger.id} charger={charger} />
        ))}
      </div>
    </div>
  );
}
