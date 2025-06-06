package TQS.project.backend;

import TQS.project.backend.dto.CreateBookingDTO;
import TQS.project.backend.entity.Booking;
import TQS.project.backend.entity.Charger;
import TQS.project.backend.entity.Client;
import TQS.project.backend.entity.Station;
import TQS.project.backend.repository.BookingRepository;
import TQS.project.backend.repository.ChargerRepository;
import TQS.project.backend.repository.ChargingSessionRepository;
import TQS.project.backend.repository.ClientRepository;
import TQS.project.backend.service.BookingService;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class BookingServiceTest {

  private ClientRepository clientRepository;
  private BookingRepository bookingRepository;
  private ChargerRepository chargerRepository;
  private ChargingSessionRepository chargingSessionRepository;

  private BookingService bookingService;

  @BeforeEach
  public void setup() {
    clientRepository = mock(ClientRepository.class);
    bookingRepository = mock(BookingRepository.class);
    chargerRepository = mock(ChargerRepository.class);
    chargingSessionRepository = mock(ChargingSessionRepository.class);
    bookingService =
        new BookingService(
            clientRepository, bookingRepository, chargerRepository, chargingSessionRepository);
  }

  @Test
  @Requirement("SCRUM-20")
  public void testCreateBooking_successful() {
    // Arrange
    CreateBookingDTO dto = new CreateBookingDTO();
    dto.setMail("test@example.com");
    dto.setChargerId(1L);
    dto.setStartTime(LocalDateTime.of(2023, 6, 1, 10, 0)); // 10:00 AM
    dto.setDuration(30);

    Client mockClient = new Client();
    mockClient.setMail("test@example.com");

    Station mockStation = new Station();
    mockStation.setId(1L);
    mockStation.setOpeningHours("08:00");
    mockStation.setClosingHours("22:00");

    Charger mockCharger = new Charger();
    mockCharger.setId(1L);
    mockCharger.setStation(mockStation);

    LocalDateTime startOfDay = dto.getStartTime().toLocalDate().atStartOfDay();
    LocalDateTime endOfDay = startOfDay.plusDays(1);

    when(clientRepository.findByMail(dto.getMail())).thenReturn(Optional.of(mockClient));
    when(chargerRepository.findById(dto.getChargerId())).thenReturn(Optional.of(mockCharger));
    when(bookingRepository.findByChargerIdAndDate(
            eq(mockCharger.getId()), eq(startOfDay), eq(endOfDay)))
        .thenReturn(Collections.emptyList());

    // Act
    String token = bookingService.createBooking(dto);

    // Assert
    assertNotNull(token);
    ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
    verify(bookingRepository, times(1)).save(captor.capture());
    Booking savedBooking = captor.getValue();

    assertEquals(mockClient, savedBooking.getUser());
    assertEquals(mockCharger, savedBooking.getCharger());
    assertEquals(dto.getDuration(), savedBooking.getDuration());
    assertEquals(dto.getStartTime(), savedBooking.getStartTime());
  }

  @Test
  @Requirement("SCRUM-20")
  public void testCreateBooking_clientNotFound_throwsException() {
    CreateBookingDTO dto = new CreateBookingDTO();
    dto.setMail("missing@example.com");
    dto.setChargerId(1L);
    dto.setStartTime(LocalDateTime.now());
    dto.setDuration(20);

    when(clientRepository.findByMail(dto.getMail())).thenReturn(Optional.empty());

    IllegalArgumentException ex =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              bookingService.createBooking(dto);
            });

    assertEquals("This email does not exist", ex.getMessage());
    verify(bookingRepository, never()).save(any());
  }

  @Test
  @Requirement("SCRUM-20")
  public void testCreateBooking_chargerNotFound_throwsException() {
    CreateBookingDTO dto = new CreateBookingDTO();
    dto.setMail("test@example.com");
    dto.setChargerId(999L);
    dto.setStartTime(LocalDateTime.now());
    dto.setDuration(20);

    when(clientRepository.findByMail(dto.getMail())).thenReturn(Optional.of(new Client()));
    when(chargerRepository.findById(dto.getChargerId())).thenReturn(Optional.empty());

    IllegalArgumentException ex =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              bookingService.createBooking(dto);
            });

    assertEquals("This charger does not exist", ex.getMessage());
    verify(bookingRepository, never()).save(any());
  }

  @Test
  @Requirement("SCRUM-20")
  public void testGetAllBookingsByDate_successful() {
    long chargerId = 1L;
    LocalDate testDate = LocalDate.of(2023, 6, 1);

    Booking booking1 = new Booking();
    booking1.setStartTime(LocalDateTime.of(testDate, LocalTime.of(10, 0)));

    Booking booking2 = new Booking();
    booking2.setStartTime(LocalDateTime.of(testDate, LocalTime.of(14, 0)));

    List<Booking> expectedBookings = Arrays.asList(booking1, booking2);

    LocalDateTime startOfDay = testDate.atStartOfDay();
    LocalDateTime endOfDay = startOfDay.plusDays(1);

    when(bookingRepository.findByChargerIdAndDate(eq(chargerId), eq(startOfDay), eq(endOfDay)))
        .thenReturn(expectedBookings);

    List<Booking> result = bookingService.getAllBookingsByDateAndCharger(chargerId, testDate);

    assertEquals(2, result.size());
    assertEquals(expectedBookings, result);
    verify(bookingRepository).findByChargerIdAndDate(eq(chargerId), eq(startOfDay), eq(endOfDay));
  }

  @Test
  @Requirement("SCRUM-20")
  public void testGetAllBookingsByDate_noBookings_returnsEmptyList() {
    long chargerId = 1L;
    LocalDate testDate = LocalDate.of(2023, 6, 1);

    LocalDateTime startOfDay = testDate.atStartOfDay();
    LocalDateTime endOfDay = startOfDay.plusDays(1);

    when(bookingRepository.findByChargerIdAndDate(eq(chargerId), eq(startOfDay), eq(endOfDay)))
        .thenReturn(Collections.emptyList());

    List<Booking> result = bookingService.getAllBookingsByDateAndCharger(chargerId, testDate);

    assertTrue(result.isEmpty());
    verify(bookingRepository).findByChargerIdAndDate(eq(chargerId), eq(startOfDay), eq(endOfDay));
  }

  @Test
  @Requirement("SCRUM-20")
  public void testCreateBooking_overlappingBookings_throwsException() {
    Client mockedClient = new Client();

    Station station = new Station();
    station.setId(1L);
    station.setOpeningHours("07:00");
    station.setClosingHours("23:00");

    Charger charger = new Charger();
    charger.setId(1L);
    charger.setStation(station);

    CreateBookingDTO dto = new CreateBookingDTO();
    dto.setMail("test@example.com");
    dto.setChargerId(1L);
    dto.setStartTime(LocalDateTime.of(2023, 6, 1, 10, 30)); // 10:30 AM
    dto.setDuration(60); // Ends at 11:30 AM

    Booking existing = new Booking();
    existing.setStartTime(LocalDateTime.of(2023, 6, 1, 10, 0));
    existing.setDuration(60);
    existing.setCharger(charger);

    LocalDateTime startOfDay = dto.getStartTime().toLocalDate().atStartOfDay();
    LocalDateTime endOfDay = startOfDay.plusDays(1);

    when(clientRepository.findByMail(dto.getMail())).thenReturn(Optional.of(mockedClient));
    when(chargerRepository.findById(dto.getChargerId())).thenReturn(Optional.of(charger));
    when(bookingRepository.findByChargerIdAndDate(
            eq(charger.getId()), eq(startOfDay), eq(endOfDay)))
        .thenReturn(Collections.singletonList(existing));

    IllegalArgumentException ex =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              bookingService.createBooking(dto);
            });

    assertEquals("The requested time slot overlaps with an existing booking", ex.getMessage());
    verify(bookingRepository, never()).save(any());
  }

  @Test
  @Requirement("SCRUM-24")
  public void testGetAllBookingsByClient_successful() {
    long clientId = 42L;

    Booking booking1 = new Booking();
    Booking booking2 = new Booking();

    List<Booking> mockBookings = List.of(booking1, booking2);

    when(bookingRepository.findAllByUserId(clientId)).thenReturn(mockBookings);

    List<Booking> result = bookingService.getAllBookingsByClient(clientId);

    assertEquals(2, result.size());
    assertEquals(mockBookings, result);
    verify(bookingRepository).findAllByUserId(clientId);
  }
}
