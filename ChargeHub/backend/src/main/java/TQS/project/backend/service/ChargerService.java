package TQS.project.backend.service;

import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import TQS.project.backend.entity.Charger;
import TQS.project.backend.entity.ChargingSession;
import TQS.project.backend.entity.Booking;
import TQS.project.backend.repository.ChargerRepository;
import TQS.project.backend.repository.BookingRepository;
import TQS.project.backend.repository.ChargingSessionRepository;

@Service
public class ChargerService {

  private ChargerRepository chargerRepository;
  private BookingRepository bookingRepository;
  private ChargingSessionRepository chargingSessionRepository;

  @Autowired
  public ChargerService(ChargerRepository chargerRepository, BookingRepository bookingRepository, ChargingSessionRepository chargingSessionRepository) {
    this.chargerRepository = chargerRepository;
    this.bookingRepository = bookingRepository;
    this.chargingSessionRepository = chargingSessionRepository;
  }

  public Optional<Charger> getChargerById(Long id) {
    return chargerRepository.findById(id);
  }

  public void startChargingSession(String token, Long chargerId) {
    Optional<Booking> optionalBooking = bookingRepository.findByToken(token);

    if (optionalBooking.isEmpty()) {
        throw new IllegalArgumentException("No booking found for the given token.");
    }

    Booking booking = optionalBooking.get();

    if (!booking.getCharger().getId().equals(chargerId)) {
        throw new IllegalArgumentException("Charger ID does not match the booking's charger.");
    }

    LocalDateTime now = LocalDateTime.now();

    if (now.isBefore(booking.getStartTime()) || now.isAfter(booking.getEndTime())) {
        throw new IllegalStateException("Current time is outside the booking time window.");
    }

    boolean sessionExists = chargingSessionRepository.existsByBooking(booking);
    if (sessionExists) {
        throw new IllegalStateException("Charging session already exists for this booking.");
    }

    ChargingSession session = new ChargingSession();
    session.setBooking(booking);
    session.setStartTime(now);
    session.setEndTime(booking.getEndTime());
    session.setEnergyConsumed(0.0f); // Placeholder value
    session.setPrice(0.0f); // Placeholder value
    session.setSessionStatus("IN PROGRESS");

    chargingSessionRepository.save(session);
  }
}
