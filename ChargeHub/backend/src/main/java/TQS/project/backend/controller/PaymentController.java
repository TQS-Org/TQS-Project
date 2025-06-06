package TQS.project.backend.controller;

import TQS.project.backend.entity.Booking;
import TQS.project.backend.repository.BookingRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import com.stripe.exception.StripeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.http.HttpStatus;
import java.util.HashMap;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

  private final BookingRepository bookingRepository;

  @Value("${stripe.api-key}")
  private String stripeSecretKey;

  public PaymentController(BookingRepository bookingRepository) {
    this.bookingRepository = bookingRepository;
  }

  @Operation(summary = "Create a Stripe checkout session for a booking payment.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description =
                "Stripe session created successfully. Contains sessionId and redirect URL.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema =
                        @Schema(
                            example =
                                """
                                {
                                  "sessionId": "cs_test_a1b2c3d4e5",
                                  "url": "https://checkout.stripe.com/pay/cs_test_a1b2c3d4e5"
                                }
                                """))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid booking token.",
            content =
                @Content(schema = @Schema(example = "{ \"error\": \"Invalid booking token\" }"))),
        @ApiResponse(
            responseCode = "500",
            description = "Stripe API error or server issue.",
            content =
                @Content(schema = @Schema(example = "{ \"error\": \"StripeException message\" }")))
      })
  @PostMapping("/create-checkout-session")
  public ResponseEntity<Map<String, String>> createCheckoutSession(
      @RequestParam String bookingToken) {
    // 1. Fetch booking
    Optional<Booking> bookingOpt = bookingRepository.findByToken(bookingToken);
    if (bookingOpt.isEmpty()) {
      return ResponseEntity.badRequest().body(Map.of("error", "Invalid booking token"));
    }

    Booking booking = bookingOpt.get();

    // 2. Calculate total price
    double pricePerMinute = booking.getCharger().getStation().getPrice();
    int duration = booking.getDuration();
    long totalAmountCents = (long) (pricePerMinute * duration * 100);

    try {
      Stripe.apiKey = stripeSecretKey;

      List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
      lineItems.add(
          SessionCreateParams.LineItem.builder()
              .setQuantity(1L)
              .setPriceData(
                  SessionCreateParams.LineItem.PriceData.builder()
                      .setCurrency("eur")
                      .setUnitAmount(totalAmountCents)
                      .setProductData(
                          SessionCreateParams.LineItem.PriceData.ProductData.builder()
                              .setName("Charge Booking #" + booking.getId())
                              .build())
                      .build())
              .build());

      SessionCreateParams params =
          SessionCreateParams.builder()
              .setMode(SessionCreateParams.Mode.PAYMENT)
              .setSuccessUrl("http://deti-tqs-23.ua.pt:3000/client/bookings") // adapt!
              .setCancelUrl("http://deti-tqs-23.ua.pt:3000/cancel") // adapt!
              .addAllLineItem(lineItems)
              .build();

      Session session = Session.create(params);

      Map<String, String> responseData = new HashMap<>();
      responseData.put("sessionId", session.getId());
      responseData.put("url", session.getUrl());
      return ResponseEntity.ok(responseData);

    } catch (StripeException e) {
      System.err.println(bookingToken + " - Error creating Stripe session: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", e.getMessage()));
    }
  }
}
