import "./Navbar.css";
import { useNavigate } from "react-router-dom";

export default function Navbar() {
  const navigate = useNavigate();

  return (
    <nav className="navbar">
      <div className="navbar-content">
        <button className="navbar-title" onClick={() => navigate("/driver")}>EV Charging App</button>
        <button className="navbar-bookings" onClick={() => navigate("/client/bookings")}>
          My Bookigs
        </button>
        <button className="navbar-login" onClick={() => navigate("/")}>
          Login
        </button>
      </div>
    </nav>
  );
}
