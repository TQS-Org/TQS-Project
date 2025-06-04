package TQS.project.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import TQS.project.backend.dto.CreateBookingDTO;
import TQS.project.backend.entity.Booking;
import TQS.project.backend.service.BookingService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

  @Autowired private BookingService bookingService;

  @PostMapping("")
  public ResponseEntity<?> createBooking(@Valid @RequestBody CreateBookingDTO dto) {
    bookingService.createBooking(dto);
    return ResponseEntity.ok("Booking created successfully!");
  }

  @GetMapping("/charger/{id}")
  public ResponseEntity<List<Booking>> getBookingsByCharger(
      @PathVariable("id") long chargerId,
      @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate date) {

    List<Booking> bookings;

    if (date != null) {
      bookings = bookingService.getAllBookingsByDateAndCharger(chargerId, date);
    } else {
      bookings = bookingService.getAllBookingsByCharger(chargerId);
    }

    return ResponseEntity.ok(bookings);
  }
}
