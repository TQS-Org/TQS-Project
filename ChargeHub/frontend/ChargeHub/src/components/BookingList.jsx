// components/BookingList.jsx
import { useEffect, useState } from "react";
import BookingCard from "./BookingCard";
import CONFIG from "../../config";

export default function BookingList({ chargerId, selectedDate }) {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchBookings = async () => {
      const token = localStorage.getItem("token");
      const formattedDate = selectedDate.toLocaleDateString("en-CA");

      try {
        const res = await fetch(
          `${CONFIG.API_URL}booking/charger/${chargerId}?date=${formattedDate}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        if (res.ok) {
          const data = await res.json();
          setBookings(data);
        } else {
          console.error("Failed to fetch bookings.");
        }
      } catch (err) {
        console.error(err);
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
