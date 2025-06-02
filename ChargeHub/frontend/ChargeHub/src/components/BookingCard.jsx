import "../css/BookingCard.css";

export default function BookingCard({ booking }) {
  return (
    <div className="booking-card">
      <h3>Booking #{booking.id}</h3>
      <p>Client: {booking.client.name}</p>
      <p>Email: {booking.client.mail}</p>
      <p>Date: {booking.date}</p>
      <p>Start: {booking.startTime}</p>
      <p>End: {booking.endTime}</p>
      <p>Status: {booking.status}</p>
    </div>
  );
}
