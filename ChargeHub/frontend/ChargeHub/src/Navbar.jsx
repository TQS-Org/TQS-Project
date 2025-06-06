import "./Navbar.css";
import { useNavigate } from "react-router-dom";
import logo from "./assets/logo.png"; // make sure the path is correct
import { useEffect, useState } from "react";

export default function Navbar() {
  const navigate = useNavigate();
  const [loggedIn, setLoggedIn] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem("token");
    setLoggedIn(!!token);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("token");
    setLoggedIn(false);
    navigate("/"); // redirect to home/login page
  };

  return (
    <nav className="navbar">
      <div className="navbar-content">
        {/* Logo only */}
        <div className="navbar-left" onClick={() => navigate("/driver")}>
          <img src={logo} alt="EV Charging App Logo" className="navbar-logo" />
        </div>

        {/* Right side buttons */}
        <div className="navbar-right">
          {loggedIn ? (
            <>
              <button className="navbar-button" onClick={() => navigate("/client/bookings")}>
                My Bookings
              </button>
              <button className="navbar-button" onClick={handleLogout}>
                Logout
              </button>
            </>
          ) : (
            <button className="navbar-button" onClick={() => navigate("/")}>
              Login
            </button>
          )}
        </div>
      </div>
    </nav>
  );
}
