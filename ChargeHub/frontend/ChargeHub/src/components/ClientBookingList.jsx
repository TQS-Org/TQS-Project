import { useEffect, useState } from "react";
import ClientBookingCard from "./ClientBookingCard";
import "../css/ClientBookingList.css";
import CONFIG from "../../config";

export default function ClientBookingList({ clientId }) {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showExpired, setShowExpired] = useState(false);

  useEffect(() => {
    const fetchBookings = async () => {
      const token = localStorage.getItem("token");
      if (!token) {
        console.error("Missing auth token.");
        setLoading(false);
        return;
      }

      try {
        const res = await fetch(`${CONFIG.API_URL}booking/client/${clientId}`, {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          credentials: "include",
        });

        if (res.ok) {
          const data = await res.json();
          setBookings(data);
        } else {
          console.error("Failed to fetch client bookings. Status:", res.status);
        }
      } catch (err) {
        console.error("Fetch error:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchBookings();
  }, [clientId]);

  const now = new Date();
  const filteredBookings = bookings.filter((booking) => {
    const endTime = new Date(booking.endTime);
    return showExpired ? endTime < now : endTime >= now;
  });

  if (loading) return <div>Loading your bookings...</div>;

  return (
    <div className="client-booking-list">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-xl font-semibold">
          {showExpired ? "Expired Bookings" : "Upcoming Bookings"}
        </h2>
        <button
          className="bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600"
          onClick={() => setShowExpired((prev) => !prev)}
        >
          {showExpired ? "Show Upcoming" : "Show Expired"}
        </button>
      </div>

      {filteredBookings.length === 0 ? (
        <div>No {showExpired ? "expired" : "upcoming"} bookings found.</div>
      ) : (
        <div className="grid gap-4">
          {filteredBookings.map((booking) => (
            <ClientBookingCard key={booking.id} booking={booking} />
          ))}
        </div>
      )}
    </div>
  );
}
