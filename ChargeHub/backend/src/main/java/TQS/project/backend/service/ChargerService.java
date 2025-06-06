package TQS.project.backend.service;

import java.util.Optional;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import TQS.project.backend.dto.ChargerDTO;
import TQS.project.backend.entity.Charger;
import TQS.project.backend.entity.Station;
import TQS.project.backend.repository.ChargerRepository;
import TQS.project.backend.repository.StationRepository;

import java.util.List;
import java.util.Optional;
import TQS.project.backend.entity.ChargingSession;
import TQS.project.backend.dto.FinishedChargingSessionDTO;
import TQS.project.backend.entity.Booking;
import TQS.project.backend.repository.ChargerRepository;
import TQS.project.backend.repository.BookingRepository;
import TQS.project.backend.repository.ChargingSessionRepository;

@Service
public class ChargerService {

  @Autowired private ChargerRepository chargerRepository;
  private BookingRepository bookingRepository;
  private StationRepository stationRepository;
  private ChargingSessionRepository chargingSessionRepository;

  @Autowired
  public ChargerService(
      ChargerRepository chargerRepository,
      BookingRepository bookingRepository,
      ChargingSessionRepository chargingSessionRepository,
      StationRepository stationRepository) {
    this.stationRepository = stationRepository;
    this.chargerRepository = chargerRepository;
    this.bookingRepository = bookingRepository;
    this.chargingSessionRepository = chargingSessionRepository;
  }

  public Optional<Charger> getChargerById(Long id) {
    return chargerRepository.findById(id);
  }

  public Charger createChargerForStation(Long stationId, ChargerDTO chargerDTO) {
    Station station =
        stationRepository
            .findById(stationId)
            .orElseThrow(() -> new RuntimeException("Station not found with ID: " + stationId));

    Charger charger = new Charger();
    charger.setType(chargerDTO.getType());
    charger.setConnectorType(chargerDTO.getConnectorType());
    charger.setPower(chargerDTO.getPower());
    charger.setAvailable(chargerDTO.getAvailable());
    charger.setStation(station);

    return chargerRepository.save(charger);
  }

  public Charger getCharger(Long id) {
    return chargerRepository
        .findById(id)
        .orElseThrow(() -> new RuntimeException("Charger not found"));
  }

  public List<Charger> getChargersByStation(Long stationId) {
    Station station =
        stationRepository
            .findById(stationId)
            .orElseThrow(() -> new RuntimeException("Station not found"));
    return chargerRepository.findByStation(station);
  }

  public Charger updateCharger(Long id, ChargerDTO dto) {
    Charger charger = getCharger(id);

    charger.setType(dto.getType());
    charger.setPower(dto.getPower());
    charger.setAvailable(dto.getAvailable());
    charger.setConnectorType(dto.getConnectorType());
    return chargerRepository.save(charger);
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

    // Use ZonedDateTime with your desired timezone
    ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Lisbon")); // or your correct zone

    // Convert booking times to ZonedDateTime in same zone before comparing:
    ZonedDateTime start = booking.getStartTime().atZone(ZoneId.of("Europe/Lisbon"));
    ZonedDateTime end = booking.getEndTime().atZone(ZoneId.of("Europe/Lisbon"));

    if (now.isBefore(start) || now.isAfter(end)) {
      throw new IllegalStateException("Current time is outside the booking time window.");
    }

    boolean sessionExists = chargingSessionRepository.existsByBooking(booking);
    if (sessionExists) {
      throw new IllegalStateException("Charging session already exists for this booking.");
    }

    ChargingSession session = new ChargingSession();
    session.setBooking(booking);
    session.setStartTime(now.toLocalDateTime());
    session.setEndTime(end.toLocalDateTime());
    session.setEnergyConsumed(0.0f);
    session.setPrice(0.0f);
    session.setSessionStatus("IN PROGRESS");

    chargingSessionRepository.save(session);

    System.out.println("Session Created: " + session);
  }

  public void finishChargingSession(long chargingSessionId, FinishedChargingSessionDTO dto) {
    ChargingSession session =
        chargingSessionRepository
            .findById(chargingSessionId)
            .orElseThrow(() -> new IllegalArgumentException("Charging session not found"));

    // Convert incoming endTime to Europe/Lisbon timezone
    ZonedDateTime endLisbon = dto.getEndTime().atZone(ZoneId.of("Europe/Lisbon"));

    System.out.println("energy: " + dto.getEnergyConsumed());
    System.out.println("Time: " + endLisbon);

    session.setEndTime(endLisbon.toLocalDateTime()); // or change entity field to ZonedDateTime
    session.setEnergyConsumed(dto.getEnergyConsumed());
    session.setSessionStatus("CONCLUDED");

    // Use price from the station
    float pricePerKWh = (float) session.getBooking().getCharger().getStation().getPrice();
    session.setPrice(dto.getEnergyConsumed() * pricePerKWh);

    chargingSessionRepository.save(session);
  }

  public Optional<ChargingSession> getChargingSessionById(long id) {
    return chargingSessionRepository.findById(id);
  }
}
