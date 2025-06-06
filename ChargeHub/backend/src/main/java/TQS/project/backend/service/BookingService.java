package TQS.project.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import TQS.project.backend.dto.CreateBookingDTO;
import TQS.project.backend.entity.Booking;
import TQS.project.backend.entity.Charger;
import TQS.project.backend.entity.ChargingSession;
import TQS.project.backend.entity.Client;
import TQS.project.backend.entity.Station;
import TQS.project.backend.repository.BookingRepository;
import TQS.project.backend.repository.ChargerRepository;
import TQS.project.backend.repository.ChargingSessionRepository;
import TQS.project.backend.repository.ClientRepository;

@Service
public class BookingService {

  private static final java.time.format.DateTimeFormatter TIME_FORMATTER =
      java.time.format.DateTimeFormatter.ofPattern("H:mm");

  private final ClientRepository clientRepository;
  private final BookingRepository bookingRepository;
  private final ChargerRepository chargerRepository;
  private final ChargingSessionRepository chargingSessionRepository;

  @Autowired
  public BookingService(
      ClientRepository clientRepository,
      BookingRepository bookingRepository,
      ChargerRepository chargerRepository,
      ChargingSessionRepository chargingSessionRepository) {
    this.clientRepository = clientRepository;
    this.bookingRepository = bookingRepository;
    this.chargerRepository = chargerRepository;
    this.chargingSessionRepository = chargingSessionRepository;
  }

  @Transactional
  public String createBooking(CreateBookingDTO dto) {
    // Validate client
    Client user =
        clientRepository
            .findByMail(dto.getMail())
            .orElseThrow(() -> new IllegalArgumentException("This email does not exist"));

    // Validate charger
    Charger charger =
        chargerRepository
            .findById(dto.getChargerId())
            .orElseThrow(() -> new IllegalArgumentException("This charger does not exist"));

    // Validate booking time
    validateBookingTime(dto.getStartTime(), dto.getDuration(), charger.getStation());

    // Check overlapping bookings
    validateNoOverlappingBookings(dto.getStartTime(), dto.getDuration(), charger.getId());

    // Create and save booking
    Booking booking = new Booking(user, charger, dto.getStartTime(), dto.getDuration());
    bookingRepository.save(booking);

    return booking.getToken();
  }

  private void validateBookingTime(LocalDateTime startTime, int duration, Station station) {
    LocalTime bookingStart = startTime.toLocalTime();
    LocalTime bookingEnd = bookingStart.plusMinutes(duration);

    LocalTime stationOpen = LocalTime.parse(station.getOpeningHours(), TIME_FORMATTER);
    LocalTime stationClose = LocalTime.parse(station.getClosingHours(), TIME_FORMATTER);

    if (bookingStart.isBefore(stationOpen)) {
      throw new IllegalArgumentException(
          "Booking cannot start before station opening time: " + station.getOpeningHours());
    }

    if (bookingEnd.isAfter(stationClose)) {
      throw new IllegalArgumentException(
          "Booking cannot end after station closing time: " + station.getClosingHours());
    }

    if (duration <= 0) {
      throw new IllegalArgumentException("Booking duration must be positive");
    }
  }

  /** Use LocalDateTime range comparison for compatibility with all databases */
  public List<Booking> getAllBookingsByDateAndCharger(long chargerId, LocalDate date) {
    LocalDateTime startOfDay = date.atStartOfDay();
    LocalDateTime endOfDay = startOfDay.plusDays(1);

    return bookingRepository.findByChargerIdAndDate(chargerId, startOfDay, endOfDay);
  }

  public List<Booking> getAllBookingsByCharger(long chargerId) {
    return bookingRepository.findAllBookingsByChargerId(chargerId);
  }

  private void validateNoOverlappingBookings(
      LocalDateTime startTime, int duration, long chargerId) {
    LocalDateTime endTime = startTime.plusMinutes(duration);

    LocalDateTime startOfDay = startTime.toLocalDate().atStartOfDay();
    LocalDateTime endOfDay = startOfDay.plusDays(1);

    List<Booking> existingBookings =
        bookingRepository.findByChargerIdAndDate(chargerId, startOfDay, endOfDay);

    for (Booking existing : existingBookings) {
      if (existing.getCharger().getId() == chargerId
          && startTime.isBefore(existing.getEndTime())
          && endTime.isAfter(existing.getStartTime())) {
        throw new IllegalArgumentException(
            "The requested time slot overlaps with an existing booking");
      }
    }
  }

  public List<Booking> getAllBookingsByClient(long clientId) {
    return bookingRepository.findAllByUserId(clientId);
  }

  public ChargingSession getChargingSessionByBookingId(Long bookingId) {
    return chargingSessionRepository.findByBookingId(bookingId);
  }

  public Booking getBookingById(long id) {
    return bookingRepository.findById(id).orElse(null);
  }
}
