package TQS.project.backend;

import TQS.project.backend.dto.CreateBookingDTO;
import TQS.project.backend.entity.Booking;
import TQS.project.backend.entity.Charger;
import TQS.project.backend.entity.Client;
import TQS.project.backend.entity.Station;
import TQS.project.backend.repository.BookingRepository;
import TQS.project.backend.repository.ChargerRepository;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class BookingServiceTest {

    private ClientRepository clientRepository;
    private BookingRepository bookingRepository;
    private ChargerRepository chargerRepository;

    private BookingService bookingService;

    @BeforeEach
    public void setup() {
        clientRepository = mock(ClientRepository.class);
        bookingRepository = mock(BookingRepository.class);
        chargerRepository = mock(ChargerRepository.class);
        bookingService = new BookingService(clientRepository, bookingRepository, chargerRepository);
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

        // Create a complete test setup with station
        Client mockClient = new Client();
        mockClient.setMail("test@example.com");

        Station mockStation = new Station();
        mockStation.setId(1L);
        mockStation.setOpeningHours("08:00");
        mockStation.setClosingHours("22:00");

        Charger mockCharger = new Charger();
        mockCharger.setId(1L);
        mockCharger.setStation(mockStation);

        when(clientRepository.findByMail(dto.getMail())).thenReturn(Optional.of(mockClient));
        when(chargerRepository.findById(dto.getChargerId())).thenReturn(Optional.of(mockCharger));
        when(bookingRepository.findByStationIdAndDate(anyLong(), any(LocalDate.class)))
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
        assertEquals(dto.getStartTime().toLocalDate(), savedBooking.getDate());
    }

    @Test
    @Requirement("SCRUM-20")
    public void testCreateBooking_clientNotFound_throwsException() {
        // Arrange
        CreateBookingDTO dto = new CreateBookingDTO();
        dto.setMail("missing@example.com");
        dto.setChargerId(1L);
        dto.setStartTime(LocalDateTime.now());
        dto.setDuration(20);

        when(clientRepository.findByMail(dto.getMail())).thenReturn(Optional.empty());

        // Act + Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(dto);
        });

        assertEquals("This email does not exist", ex.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @Requirement("SCRUM-20")
    public void testCreateBooking_chargerNotFound_throwsException() {
        // Arrange
        CreateBookingDTO dto = new CreateBookingDTO();
        dto.setMail("test@example.com");
        dto.setChargerId(999L);
        dto.setStartTime(LocalDateTime.now());
        dto.setDuration(20);

        when(clientRepository.findByMail(dto.getMail())).thenReturn(Optional.of(new Client()));
        when(chargerRepository.findById(dto.getChargerId())).thenReturn(Optional.empty());

        // Act + Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(dto);
        });

        assertEquals("This charger does not exist", ex.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @Requirement("SCRUM-20")
    public void testGetAllBookingsByDate_successful() {
        // Arrange
        long stationId = 1L;
        LocalDate testDate = LocalDate.of(2023, 6, 1);
        
        Booking booking1 = new Booking();
        booking1.setStartTime(LocalDateTime.of(testDate, LocalTime.of(10, 0)));
        
        Booking booking2 = new Booking();
        booking2.setStartTime(LocalDateTime.of(testDate, LocalTime.of(14, 0)));
        
        List<Booking> expectedBookings = Arrays.asList(booking1, booking2);
        
        when(bookingRepository.findByStationIdAndDate(stationId, testDate))
            .thenReturn(expectedBookings);
    
        // Act
        List<Booking> result = bookingService.getAllBookingsByDateAndStation(stationId, testDate);
    
        // Assert
        assertEquals(2, result.size());
        assertEquals(expectedBookings, result);
        verify(bookingRepository).findByStationIdAndDate(stationId, testDate);
    }
    
    @Test
    @Requirement("SCRUM-20")
    public void testGetAllBookingsByDate_noBookings_returnsEmptyList() {
        // Arrange
        long stationId = 1L;
        LocalDate testDate = LocalDate.of(2023, 6, 1);
        
        when(bookingRepository.findByStationIdAndDate(stationId, testDate))
            .thenReturn(Collections.emptyList());
    
        // Act
        List<Booking> result = bookingService.getAllBookingsByDateAndStation(stationId, testDate);
    
        // Assert
        assertTrue(result.isEmpty());
        verify(bookingRepository).findByStationIdAndDate(stationId, testDate);
    }

    @Test
    @Requirement("SCRUM-20")
    public void testCreateBooking_overlappingBookings_throwsException() {
        Client mockedClient = new Client();

        Station station = new Station();
        station.setId(1L);
        station.setName("Filtered");
        station.setAddress("Lisboa");
        station.setPrice(0.35);
        station.setOpeningHours("07:00");
        station.setClosingHours("23:00");

        Charger charger = new Charger();
        charger.setId(1L);
        charger.setType("FAST");
        charger.setConnectorType("CCS");
        charger.setPower(100.0);
        charger.setAvailable(true);
        charger.setStation(station);
        // Arrange
        CreateBookingDTO dto = new CreateBookingDTO();
        dto.setMail("test@example.com");
        dto.setChargerId(1L);
        dto.setStartTime(LocalDateTime.of(2023, 6, 1, 10, 30)); // 10:30 AM
        dto.setDuration(60); // Ends at 11:30 AM

        // Existing booking from 10:00 AM to 11:00 AM
        Booking existing = new Booking();
        existing.setStartTime(LocalDateTime.of(2023, 6, 1, 10, 0));
        existing.setDuration(60);
        existing.setCharger(charger);

        when(clientRepository.findByMail(dto.getMail())).thenReturn(Optional.of(mockedClient));
        when(chargerRepository.findById(dto.getChargerId())).thenReturn(Optional.of(charger));
        when(bookingRepository.findByStationIdAndDate(anyLong(), any(LocalDate.class)))
            .thenReturn(Collections.singletonList(existing));

        // Act + Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(dto);
        });

        assertEquals("The requested time slot overlaps with an existing booking", ex.getMessage());
        verify(bookingRepository, never()).save(any());
    }
}

