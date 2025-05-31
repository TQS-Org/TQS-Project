package TQS.project.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import TQS.project.backend.dto.CreateBookingDTO;
import TQS.project.backend.entity.Client;
import TQS.project.backend.service.BookingService;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

  @Autowired private BookingService bookingService;

  @PostMapping("")
  public ResponseEntity<?> createBooking(@Valid @RequestBody CreateBookingDTO dto) {
    return null;
  }

  //@PostMapping("")
  //public ResponseEntity<?> createBooking(@Valid @RequestBody CreateBookingDTO dto) {
  //  try {
  //    bookingService.createBooking(dto);
  //    return ResponseEntity.ok("Operator account created successfully.");
  //  } catch (IllegalArgumentException e) {
  //    return ResponseEntity.badRequest().body(e.getMessage());
  //  }
  //}

  //@GetMapping("/operators")
  //public ResponseEntity<List<Staff>> getAllOperators() {
  //  List<Staff> operators = staffService.getAllOperators();
  //  return ResponseEntity.ok(operators);
  //}
}
