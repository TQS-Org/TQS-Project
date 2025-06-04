import { useEffect, useState } from "react";
import BookingCard from "./BookingCard";
import CONFIG from "../../config";

export default function BookingList({ chargerId, selectedDate }) {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchBookings = async () => {
      const token = localStorage.getItem("token");
      if (!token) {
        console.error("Missing auth token.");
        setLoading(false);
        return;
      }

      const formattedDate = selectedDate.toISOString().split("T")[0]; // 'YYYY-MM-DD'
      try {
        const res = await fetch(
          `${CONFIG.API_URL}booking/charger/${chargerId}?date=${formattedDate}`,
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
            credentials: "include",
          }
        );

        if (res.ok) {
          const data = await res.json();
          setBookings(data);
        } else {
          console.error("Failed to fetch bookings. Status:", res.status);
        }
      } catch (err) {
        console.error("Fetch error:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchBookings();
  }, [chargerId, selectedDate]);

  if (loading) return <div>Loading bookings...</div>;
  if (bookings.length === 0) return <div>No bookings for this day.</div>;

  return (
    <div className="booking-list">
      <h2>Bookings on {selectedDate.toDateString()}</h2>
      <div className="booking-list-grid">
        {bookings.map((booking) => (
          <BookingCard key={booking.id} booking={booking} />
        ))}
      </div>
    </div>
  );
}
