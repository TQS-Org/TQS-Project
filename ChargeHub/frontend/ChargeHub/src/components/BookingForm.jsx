import { useState, useEffect } from "react";

// helper to format LocalDateTime string (no timezone)
function formatLocalDateTime(date) {
  const pad = (n) => n.toString().padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}:00`;
}

export default function BookingForm({ station, chargerId, onSubmit }) {
  const [startTime, setStartTime] = useState("");
  const [duration, setDuration] = useState(5);
  const [timeOptions, setTimeOptions] = useState([]);
  const [feedback, setFeedback] = useState(null);

  const email = localStorage.getItem("email");

  useEffect(() => {
    if (!station) return;

    const generateTimeSlots = () => {
      const result = [];
      const [openH, openM = 0] = station.openingHours.split(":").map(Number);
      const [closeH, closeM = 0] = station.closingHours.split(":").map(Number);

      const today = new Date();
      const start = new Date(today);
      start.setHours(openH, openM, 0, 0);

      const end = new Date(today);
      end.setHours(closeH, closeM - 5, 0, 0); // Subtract 5 mins from closing

      while (start <= end) {
        result.push(start.toTimeString().slice(0, 5));
        start.setMinutes(start.getMinutes() + 5);
      }

      return result;
    };

    setTimeOptions(generateTimeSlots());
  }, [station]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!startTime) return;

    const today = new Date();
    const [hour, minute] = startTime.split(":");
    today.setHours(hour, minute, 0, 0);

    const dto = {
      mail: email,
      chargerId,
      startTime: formatLocalDateTime(today), // local format
      duration: parseInt(duration),
    };

    try {
      const successMessage = await onSubmit(dto);
      setFeedback({ type: "success", message: successMessage });
    } catch (errorMessage) {
      setFeedback({ type: "error", message: errorMessage });
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-3 border p-4 rounded shadow">
      <h2 className="text-lg font-semibold">Create Booking</h2>

      <div>
        <label className="block mb-1">Start Time</label>
        <select
          value={startTime}
          onChange={(e) => setStartTime(e.target.value)}
          className="w-full p-1 border rounded"
          required
        >
          <option value="" disabled>Select time</option>
          {timeOptions.map((time) => (
            <option key={time} value={time}>{time}</option>
          ))}
        </select>
      </div>

      <div>
        <label className="block mb-1">Duration (min)</label>
        <input
          type="number"
          min={5}
          max={60}
          step={5}
          value={duration}
          onChange={(e) => setDuration(e.target.value)}
          className="w-full p-1 border rounded"
          required
        />
      </div>

      <button type="submit" className="mt-2 px-4 py-2 bg-blue-600 text-white rounded">
        Confirm Booking
      </button>

      {feedback && (
        <div
          className={`mt-3 p-2 rounded text-sm ${
            feedback.type === "success" ? "bg-green-100 text-green-800" : "bg-red-100 text-red-800"
          }`}
        >
          {feedback.message}
        </div>
      )}
    </form>
  );
}
