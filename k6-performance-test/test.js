import http from "k6/http";
import { check } from "k6";

const BASE_URL = "http://deti-tqs-23.ua.pt:8080";

export const options = {
  stages: [
    { duration: "10s", target: 1 }, 
    { duration: "20s", target: 1 },  
    { duration: "10s", target: 0 },   
  ],
};

const chargerId = 2;
const bookingDuration = 5; 

const openHour = 7;  
const closeHour = 23; 

let currentDate = new Date();
currentDate.setHours(0, 0, 0, 0);
currentDate.setDate(currentDate.getDate() + 50);

let currentSlotMinutes = openHour * 60; 

function formatDateTime(date) {
  return date.toISOString().slice(0, 19);
}

function getStartTime() {
  let dt = new Date(currentDate.getTime());
  dt.setMinutes(currentSlotMinutes);
  dt.setSeconds(0);
  dt.setMilliseconds(0);
  return dt;
}

function incrementSlot() {
  currentSlotMinutes += bookingDuration;
  if (currentSlotMinutes >= closeHour * 60) {
    currentDate.setDate(currentDate.getDate() + 1);
    currentDate.setHours(0, 0, 0, 0);
    currentSlotMinutes = openHour * 60;
  }
}

export default function () {
  const startTime = getStartTime();
  const bookingData = {
    mail: "driver@mail.com",
    chargerId: chargerId,
    startTime: formatDateTime(startTime),
    duration: bookingDuration,
  };

  let res = http.post(`${BASE_URL}/api/booking`, JSON.stringify(bookingData), {
    headers: {
      "Content-Type": "application/json",
      "Authorization": "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkcml2ZXJAbWFpbC5jb20iLCJyb2xlIjoiRVZfRFJJVkVSIiwiaWF0IjoxNzQ5MTM5NzU5LCJleHAiOjE3NDkxNDMzNTl9.1PxOM4Lmdi2JZ87r3X8M9bzPofJPh9VGw12Tu-ZBtEXLDJzamOWEdj1PszJsVJxKJBNswOhxj0Yh-k0VMtDRUQ",
    },
  });

  console.log(`Booking for ${bookingData.startTime}, charger ${chargerId}: ${res.status}`);

  incrementSlot();
}
