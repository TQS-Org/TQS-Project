package TQS.project.backend;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import TQS.project.backend.entity.Charger;
import TQS.project.backend.entity.Station;
import TQS.project.backend.dto.FinishedChargingSessionDTO;
import TQS.project.backend.entity.Booking;
import TQS.project.backend.entity.ChargingSession;
import TQS.project.backend.repository.ChargerRepository;
import TQS.project.backend.repository.BookingRepository;
import TQS.project.backend.repository.ChargingSessionRepository;
import TQS.project.backend.service.ChargerService;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.mockito.quality.Strictness;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.Optional;

import TQS.project.backend.entity.Booking;
import TQS.project.backend.entity.ChargingSession;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ChargerServiceTest {

  @Mock private ChargerRepository chargerRepository;
  @Mock private BookingRepository bookingRepository;
  @Mock private ChargingSessionRepository chargingSessionRepository;

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
  @Requirement("SCRUM-26")
  void finishChargingSession_validSession_updatesFieldsCorrectly() {
    ChargingSession session = new ChargingSession();
    session.setId(10L);
    session.setStartTime(LocalDateTime.now().minusHours(1));
    session.setSessionStatus("ONGOING");

    FinishedChargingSessionDTO dto = new FinishedChargingSessionDTO(15.5f, LocalDateTime.now());

    when(chargingSessionRepository.findById(10L)).thenReturn(Optional.of(session));

    chargerService.finishChargingSession(10L, dto);

    assertThat(session.getEnergyConsumed()).isEqualTo(15.5f);
    assertThat(session.getEndTime()).isEqualTo(dto.getEndTime());
    assertThat(session.getSessionStatus()).isEqualTo("CONCLUDED");
    assertThat(session.getPrice()).isEqualTo(15.5f * 0.25f); // price logic
    verify(chargingSessionRepository).save(session);
  }

  @Test
  @Requirement("SCRUM-26")
  void finishChargingSession_invalidSessionId_throwsException() {
    FinishedChargingSessionDTO dto = new FinishedChargingSessionDTO(10.0f, LocalDateTime.now());

    when(chargingSessionRepository.findById(999L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> chargerService.finishChargingSession(999L, dto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Charging session not found");
  }

}
