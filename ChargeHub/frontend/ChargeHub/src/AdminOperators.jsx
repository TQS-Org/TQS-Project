import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./AdminOperators.css";

export default function AdminOperators() {
  const [operators, setOperators] = useState([]);
  const [search, setSearch] = useState("");
  const [sort, setSort] = useState("az");
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("token");
    fetch("http://localhost:8080/api/staff/operators", {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => res.json())
      .then(setOperators);
  }, []);

  const filteredAndSorted = [...operators]
    .filter((op) => op.mail.toLowerCase().includes(search.toLowerCase()))
    .sort((a, b) =>
      sort === "az"
        ? a.name.localeCompare(b.name)
        : b.name.localeCompare(a.name)
    );

  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");


  return (
    <div className="operator-section">
      <div className="operator-controls">
        <input
          type="text"
          placeholder="Search by email..."
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
        <p>Create Operator</p>
        </div>


        {filteredAndSorted.map((op) => (
          <div key={op.id} className="operator-card">
            <h3>{op.name}</h3>
            <p><strong>Email:</strong> {op.mail}</p>
            <p><strong>Phone:</strong> {op.number}</p>
            <p><strong>Address:</strong> {op.address}</p>
          </div>
        ))}
      </div>
      {showCreateModal && (
        <div className="modal-overlay">
            <div className="modal">
            <h2>Create Operator</h2>
            <form
                onSubmit={(e) => {
                e.preventDefault();
                const form = e.target;
                const dto = {
                    name: form.name.value,
                    mail: form.mail.value,
                    password: form.password.value,
                    age: form.age.value,
                    number: form.number.value,
                    address: form.address.value
                };

                const token = localStorage.getItem("token");
                fetch("http://localhost:8080/api/staff/operator", {
                    method: "POST",
                    headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`
                    },
                    body: JSON.stringify(dto)
                })
                    .then((res) => {
                    if (!res.ok) return res.text().then((text) => { throw new Error(text); });
                    return res.text();
                    })
                    .then(() => {
                    setShowCreateModal(false);
                    setErrorMsg("");
                    setShowSuccessModal(true);

                    // Instead of reloading the whole page, just re-fetch operators
                    fetch("http://localhost:8080/api/staff/operators", {
                      headers: { Authorization: `Bearer ${token}` },
                    })
                      .then((res) => res.json())
                      .then(setOperators);
                    })
                    .catch((err) => {
                    setErrorMsg(err.message || "Something went wrong");
                    });
                }}
            >
                <input name="name" placeholder="Name" required />
                <input name="mail" placeholder="Email" required type="email" />
                <input name="password" placeholder="Password" required type="password" />
                <input name="age" placeholder="Age" required type="number" />
                <input name="number" placeholder="Phone Number" />
                <input name="address" placeholder="Address" />

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
            <h3>Operator Created!</h3>
            <button type="button" onClick={() => setShowSuccessModal(false)}>Close</button>
          </div>
        </div>
      )}

    </div>
  );
}
