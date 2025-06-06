package TQS.project.backend;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import TQS.project.backend.dto.ChargerDTO;
import TQS.project.backend.entity.Charger;
import TQS.project.backend.entity.ChargingSession;
import TQS.project.backend.entity.Station;
import TQS.project.backend.dto.FinishedChargingSessionDTO;
import TQS.project.backend.entity.Booking;
import TQS.project.backend.repository.ChargerRepository;
import TQS.project.backend.repository.StationRepository;
import TQS.project.backend.repository.BookingRepository;
import TQS.project.backend.repository.ChargingSessionRepository;
import TQS.project.backend.service.ChargerService;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.List;

import org.mockito.quality.Strictness;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.Optional;

import TQS.project.backend.entity.Booking;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ChargerServiceTest {

  @Mock private ChargerRepository chargerRepository;
  @Mock private BookingRepository bookingRepository;
  @Mock private ChargingSessionRepository chargingSessionRepository;

  @Mock private StationRepository stationRepository;

  @InjectMocks private ChargerService chargerService;

  @Test
  @Requirement("SCRUM-20")
  void getChargerById_existingId_returnsCharger() {
    Station station = new Station();

    Charger charger = new Charger();
    charger.setStation(station);
    charger.setId(1L);
    charger.setType("DC");

    when(chargerRepository.findById(1L)).thenReturn(Optional.of(charger));

    Optional<Charger> result = chargerService.getChargerById(1L);

    assertThat(result.isPresent()).isTrue();
    assertThat(result.get().getType()).isEqualTo("DC");
  }

  @Test
  @Requirement("SCRUM-20")
  void getChargerById_nonExistingId_returnsEmpty() {
    when(chargerRepository.findById(2L)).thenReturn(Optional.empty());

    Optional<Charger> result = chargerService.getChargerById(2L);

    assertThat(result).isEmpty();
  }

  @Test
  @Requirement("SCRUM-36")
  void createChargerForStation_success() {
    Station station = new Station();
    station.setId(1L);

    ChargerDTO dto = new ChargerDTO();
    dto.setType("DC");
    dto.setConnectorType("CCS");
    dto.setPower(50.0);
    dto.setAvailable(true);

    Charger chargerToSave = new Charger();
    chargerToSave.setType(dto.getType());
    chargerToSave.setConnectorType(dto.getConnectorType());
    chargerToSave.setPower(dto.getPower());
    chargerToSave.setAvailable(dto.getAvailable());
    chargerToSave.setStation(station);

    Charger savedCharger = new Charger();
    savedCharger.setId(1L);
    savedCharger.setType(dto.getType());
    savedCharger.setConnectorType(dto.getConnectorType());
    savedCharger.setPower(dto.getPower());
    savedCharger.setAvailable(dto.getAvailable());
    savedCharger.setStation(station);

    when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
    when(chargerRepository.save(any(Charger.class))).thenReturn(savedCharger);

    Charger result = chargerService.createChargerForStation(1L, dto);

    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getType()).isEqualTo("DC");
  }

  @Test
  @Requirement("SCRUM-36")
  void createChargerForStation_stationNotFound_throwsException() {
    ChargerDTO dto = new ChargerDTO();
    dto.setType("DC");
    dto.setConnectorType("CCS");
    dto.setPower(50.0);
    dto.setAvailable(true);

    when(stationRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> chargerService.createChargerForStation(1L, dto));
  }

  @Test
  @Requirement("SCRUM-36")
  void getCharger_existingId_returnsCharger() {
    Charger charger = new Charger();
    charger.setId(1L);
    charger.setType("AC");

    when(chargerRepository.findById(1L)).thenReturn(Optional.of(charger));

    Charger result = chargerService.getCharger(1L);

    assertThat(result).isNotNull();
    assertThat(result.getType()).isEqualTo("AC");
  }

  @Test
  @Requirement("SCRUM-36")
  void getCharger_nonExistingId_throwsException() {
    when(chargerRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> chargerService.getCharger(1L));
  }

  @Test
  @Requirement("SCRUM-36")
  void getChargersByStation_success() {
    Station station = new Station();
    station.setId(1L);

    Charger charger1 = new Charger();
    charger1.setId(1L);
    charger1.setStation(station);

    Charger charger2 = new Charger();
    charger2.setId(2L);
    charger2.setStation(station);

    when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
    when(chargerRepository.findByStation(station)).thenReturn(List.of(charger1, charger2));

    List<Charger> result = chargerService.getChargersByStation(1L);

    assertThat(result).hasSize(2);
  }

  @Test
  @Requirement("SCRUM-36")
  void getChargersByStation_stationNotFound_throwsException() {
    when(stationRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> chargerService.getChargersByStation(1L));
  }

  @Test
  @Requirement("SCRUM-36")
  void updateCharger_success() {
    Charger existing = new Charger();
    existing.setId(1L);
    existing.setType("AC");

    ChargerDTO dto = new ChargerDTO();
    dto.setType("DC");
    dto.setConnectorType("CCS");
    dto.setPower(50.0);
    dto.setAvailable(true);

    when(chargerRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(chargerRepository.save(any(Charger.class))).thenReturn(existing);

    Charger result = chargerService.updateCharger(1L, dto);

    assertThat(result.getType()).isEqualTo("DC");
    assertThat(result.getConnectorType()).isEqualTo("CCS");
    assertThat(result.getPower()).isEqualTo(50.0);
    assertThat(result.getAvailable()).isTrue();
  }

  @Disabled("Temporarily disabled due to problems with LocalDateTime on CI pipeline reason")
  @Test
  @Requirement("SCRUM-24")
  void startChargingSession_validBooking_createsSession() {
    Charger charger = new Charger();
    charger.setId(1L);

    Booking booking = new Booking();
    booking.setToken("VALIDTOKEN");
    booking.setStartTime(LocalDateTime.now().minusMinutes(10));
    booking.setEndTime(LocalDateTime.now().plusMinutes(10));
    booking.setCharger(charger);

    when(bookingRepository.findByToken("VALIDTOKEN")).thenReturn(Optional.of(booking));
    when(chargingSessionRepository.existsByBooking(booking)).thenReturn(false);

    chargerService.startChargingSession("VALIDTOKEN", 1L);

    verify(chargingSessionRepository).save(any(ChargingSession.class));
  }

  @Test
  @Requirement("SCRUM-24")
  void startChargingSession_invalidToken_throwsException() {
    when(bookingRepository.findByToken("INVALID")).thenReturn(Optional.empty());

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          chargerService.startChargingSession("INVALID", 1L);
        });
  }

  @Test
  @Requirement("SCRUM-24")
  void startChargingSession_wrongChargerId_throwsException() {
    Charger charger = new Charger();
    charger.setId(2L); // expected != actual

    Booking booking = new Booking();
    booking.setToken("TOKEN");
    booking.setCharger(charger);

    when(bookingRepository.findByToken("TOKEN")).thenReturn(Optional.of(booking));

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          chargerService.startChargingSession("TOKEN", 1L);
        });
  }

  @Test
  @Requirement("SCRUM-24")
  void startChargingSession_outsideTimeWindow_throwsException() {
    Charger charger = new Charger();
    charger.setId(1L);

    Booking booking = new Booking();
    booking.setToken("TOKEN");
    booking.setCharger(charger);
    booking.setStartTime(LocalDateTime.of(2099, 1, 1, 0, 0));
    booking.setEndTime(LocalDateTime.of(2099, 1, 2, 0, 0));

    when(bookingRepository.findByToken("TOKEN")).thenReturn(Optional.of(booking));

    assertThrows(
        IllegalStateException.class,
        () -> {
          chargerService.startChargingSession("TOKEN", 1L);
        });
  }

  @Test
  @Requirement("SCRUM-24")
  void startChargingSession_sessionAlreadyExists_throwsException() {
    Charger charger = new Charger();
    charger.setId(1L);

    Booking booking = new Booking();
    booking.setToken("TOKEN");
    booking.setCharger(charger);
    booking.setStartTime(LocalDateTime.now().minusMinutes(5));
    booking.setEndTime(LocalDateTime.now().plusMinutes(5));

    when(bookingRepository.findByToken("TOKEN")).thenReturn(Optional.of(booking));
    when(chargingSessionRepository.existsByBooking(booking)).thenReturn(true);

    assertThrows(
        IllegalStateException.class,
        () -> {
          chargerService.startChargingSession("TOKEN", 1L);
        });
  }

  @Test
  @Requirement("SCRUM-27")
  void finishChargingSession_validSession_updatesFieldsCorrectly() {

    Station station = new Station();
    station.setPrice(30);

    Charger charger = new Charger();
    charger.setStation(station);
    charger.setId(1L);
    charger.setType("DC");

    Booking booking = new Booking();
    booking.setToken("VALIDTOKEN");
    booking.setStartTime(LocalDateTime.now().minusMinutes(20));
    booking.setEndTime(LocalDateTime.now().plusMinutes(20));
    booking.setCharger(charger);

    ChargingSession session = new ChargingSession();
    session.setId(10L);
    session.setStartTime(LocalDateTime.now().minusHours(1));
    session.setSessionStatus("IN PROGRESS");
    session.setBooking(booking);

    FinishedChargingSessionDTO dto = new FinishedChargingSessionDTO(15.5f, LocalDateTime.now());

    when(chargingSessionRepository.findById(10L)).thenReturn(Optional.of(session));

    chargerService.finishChargingSession(10L, dto);

    assertThat(session.getEnergyConsumed()).isEqualTo(15.5f);
    assertThat(session.getEndTime()).isEqualTo(dto.getEndTime());
    assertThat(session.getSessionStatus()).isEqualTo("CONCLUDED");
    assertEquals(session.getPrice(), 15.5f * station.getPrice());
    verify(chargingSessionRepository).save(session);
  }

  @Test
  @Requirement("SCRUM-27")
  void finishChargingSession_invalidSessionId_throwsException() {
    FinishedChargingSessionDTO dto = new FinishedChargingSessionDTO(10.0f, LocalDateTime.now());

    when(chargingSessionRepository.findById(999L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> chargerService.finishChargingSession(999L, dto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Charging session not found");
  }
}
