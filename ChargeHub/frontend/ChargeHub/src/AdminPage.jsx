import { useState } from "react";
import AdminOperators from "./AdminOperators";
import AdminStations from "./AdminStations"; // New import
import "./AdminPage.css";

export default function AdminPage() {
  const [activeSection, setActiveSection] = useState("operators");

  return (
    <div className="admin-page">
      <nav className="admin-sidebar">
        <h2>Admin</h2>
        <ul>
          <li
            className={activeSection === "operators" ? "active" : ""}
            onClick={() => setActiveSection("operators")}
          >
            Show Operators
          </li>
          <li
            className={activeSection === "stations" ? "active" : ""}
            onClick={() => setActiveSection("stations")}
          >
            Show Stations
          </li>
        </ul>
      </nav>

      <main className="admin-content">
        {activeSection === "operators" && <AdminOperators />}
        {activeSection === "stations" && <AdminStations />}
      </main>
    </div>
  );
}
