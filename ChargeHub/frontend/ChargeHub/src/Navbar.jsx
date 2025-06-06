import "./Navbar.css";
import { useNavigate } from "react-router-dom";
import logo from "./assets/logo.png";
import { useEffect, useState } from "react";
import CONFIG from "../config";

export default function Navbar() {
  const navigate = useNavigate();
  const [loggedIn, setLoggedIn] = useState(false);
  const [role, setRole] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      setLoggedIn(true);

      // Validate token and get user role
      const fetchRole = async () => {
        try {
          const res = await fetch(`${CONFIG.API_URL}auth/validate`, {
            headers: { Authorization: `Bearer ${token}` },
          });
          if (res.ok) {
            const data = await res.json();
            setRole(data.role);
          }
        } catch (err) {
          console.error(err);
        }
      };

      fetchRole();
    }
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("token");
    setLoggedIn(false);
    setRole(null);
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
              {role === "EV_DRIVER" && (
                <button
                  className="navbar-button"
                  onClick={() => navigate("/client/bookings")}
                >
                  My Bookings
                </button>
              )}
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
