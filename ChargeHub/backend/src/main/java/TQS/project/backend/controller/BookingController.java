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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

  @Autowired private BookingService bookingService;

  @Operation(summary = "Create a new booking.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Booking created successfully.",
            content = @Content(schema = @Schema(example = "Booking created successfully!"))),
        @ApiResponse(responseCode = "400", description = "Invalid input data.", content = @Content)
      })
  @PostMapping("")
  public ResponseEntity<?> createBooking(@Valid @RequestBody CreateBookingDTO dto) {
    bookingService.createBooking(dto);
    return ResponseEntity.ok("Booking created successfully!");
  }

  @Operation(summary = "Get bookings for a specific charger, optionally filtered by date.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of bookings retrieved successfully.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Booking.class))),
        @ApiResponse(
            responseCode = "404",
            description = "No bookings found for given charger.",
            content = @Content)
      })
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

  @GetMapping("/client/{id}")
  public ResponseEntity<List<Booking>> getBookingsByCharger(@PathVariable("id") long clientId) {

    List<Booking> bookings = bookingService.getAllBookingsByClient(clientId);

    return ResponseEntity.ok(bookings);
  }
}
