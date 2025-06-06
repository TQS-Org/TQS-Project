package TQS.project.backend.controller;

import TQS.project.backend.entity.Booking;
import TQS.project.backend.repository.BookingRepository;
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

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("http://localhost:3000/client/bookings") // adapt!
                    .setCancelUrl("http://localhost:3000/cancel") // adapt!
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
