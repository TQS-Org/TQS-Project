import "./Navbar.css";
import { useNavigate } from "react-router-dom";

export default function Navbar() {
  const navigate = useNavigate();

  return (
    <nav className="navbar">
      <div className="navbar-content">
        <div className="navbar-title">EV Charging App</div>
        <button className="navbar-login" onClick={() => navigate("/")}>
          Login
        </button>
      </div>
    </nav>
  );
}
