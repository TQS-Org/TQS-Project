import { useEffect, useState } from "react";
import CONFIG from "../../config";
import "../css/AssignStationModal.css";

export default function AssignStationModal({ operator, onClose, onAssigned }) {
  const [stations, setStations] = useState([]);
  const [selectedStationId, setSelectedStationId] = useState("");
  const [errorMsg, setErrorMsg] = useState("");
  const [successMsg, setSuccessMsg] = useState("");

  useEffect(() => {
    const token = localStorage.getItem("token");
    fetch(`${CONFIG.API_URL}stations`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => res.json())
      .then(setStations)
      .catch(() => setStations([]));
  }, []);

  const handleAssign = (e) => {
    e.preventDefault();

    if (!selectedStationId) {
      setErrorMsg("Please select a station");
      return;
    }

    const dto = {
      operatorId: operator.id,
      stationId: parseInt(selectedStationId),
    };

    const token = localStorage.getItem("token");
    fetch(`${CONFIG.API_URL}staff/operator/assign-station`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(dto),
    })
      .then((res) => {
        if (!res.ok) {
          return res.text().then((text) => { throw new Error(text); });
        }
        return res.text();
      })
      .then(() => {
        setErrorMsg("");
        setSuccessMsg("Station assigned successfully!"); // ✅ success message
        onAssigned(); // Trigger refresh in parent
        setTimeout(() => {
          setSuccessMsg("");
          onClose();
        }, 1500); // Auto-close after 1.5s
      })
      .catch((err) => {
        setErrorMsg(err.message || "Failed to assign station");
        setSuccessMsg("");
      });
  };

  if (!operator) return null;

  return (
    <div className="modal-overlay">
      <div className="modal">
        <h2>Assign Station to {operator.name}</h2>

        {operator.assignedStation ? (
          <div className="assigned-station-box">
            <p><strong>Currently Assigned Station:</strong></p>
            <p>{operator.assignedStation.name} ({operator.assignedStation.address})</p>
          </div>
        ) : (
          <p><em>No station currently assigned</em></p>
        )}

        <form onSubmit={handleAssign}>
          <select
            value={selectedStationId}
            onChange={(e) => setSelectedStationId(e.target.value)}
            required
          >
            <option value="">-- Select a station --</option>
            {stations.map((st) => (
              <option key={st.id} value={st.id}>
                {st.name}
              </option>
            ))}
          </select>

          {errorMsg && <p className="error-msg">{errorMsg}</p>}
          {successMsg && <p className="success-msg">{successMsg}</p>} {/* ✅ show success */}

          <div className="modal-buttons">
            <button type="submit">Assign</button>
            <button
              type="button"
              className="cancel-button"
              onClick={onClose}
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
