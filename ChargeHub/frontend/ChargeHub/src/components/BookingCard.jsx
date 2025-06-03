import "../css/BookingCard.css";

export default function BookingCard({ booking }) {
  console.log("BookingCard data:", JSON.stringify(booking, null, 2));

  const user = booking.user || { name: "Unknown", mail: "Unknown" };

  return (
    <div className="booking-card">
      <h3>Booking #{booking.id}</h3>
      <p>Client: {user.name}</p>
      <p>Email: {user.mail}</p>
      <p>Date: {booking.date}</p>
      <p>Start: {booking.startTime}</p>
      <p>End: {booking.endTime}</p>
      <p>Status: {booking.status}</p>
    </div>
  );
}


