import "../css/BookingCard.css";

export default function BookingCard({ booking }) {
  console.log("BookingCard data:", JSON.stringify(booking, null, 2));

  const user = booking.user || { name: "Unknown", mail: "Unknown" };

  // Extract just the HH:MM part from ISO datetime
  const formatTime = (isoString) => {
    const date = new Date(isoString);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  };

  return (
    <div className="booking-card">
      <h3>Booking</h3>
      <p>Client: {user.name}</p>
      <p>Date: {booking.date}</p>
      <p>Start: {formatTime(booking.startTime)}</p>
      <p>End: {formatTime(booking.endTime)}</p>
    </div>
  );
}
