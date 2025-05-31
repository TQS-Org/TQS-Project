package TQS.project.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import TQS.project.backend.dto.CreateBookingDTO;
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
}
