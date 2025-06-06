import { useEffect, useState } from "react";
import ClientBookingList from "./components/ClientBookingList";
import "./ClientBookingsPage.css";
import { useNavigate } from "react-router-dom";
import CONFIG from "../config";

export default function ClientBookingsPage() {
  const [clientId, setClientId] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchClientId = async () => {
      const token = localStorage.getItem("token");
      const mail = localStorage.getItem("email");

      if (!token || !mail) {
        navigate("/"); // Redirect if token or email is missing
        return;
      }

      try {
        const res = await fetch(`${CONFIG.API_URL}client/${mail}`, {
          headers: { 
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
        });

        if (!res.ok) {
          throw new Error(res.status === 404 ? "Client not found" : "Failed to fetch client");
        }

        const data = await res.json();
        setClientId(data.id); 
      } catch (err) {
        console.error("Error fetching client:", err);
        setError(err.message);
        navigate("/");
      } finally {
        setLoading(false);
      }
    };

    fetchClientId();
  }, [navigate]);

  if (loading) return <div>Loading...</div>;
  if (error) return <div>{error}</div>;
  if (!clientId) return <div>Could not retrieve your user information.</div>;

  return (
    <div className="max-w-4xl mx-auto mt-8 px-4">
      <h1 className="text-2xl font-bold mb-6">My Bookings</h1>
      <ClientBookingList clientId={clientId} />
    </div>
  );
}