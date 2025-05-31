package TQS.project.backend;

import TQS.project.backend.dto.CreateBookingDTO;
import TQS.project.backend.entity.Booking;
import TQS.project.backend.entity.Charger;
import TQS.project.backend.entity.Client;
import TQS.project.backend.repository.BookingRepository;
import TQS.project.backend.repository.ChargerRepository;
import TQS.project.backend.repository.ClientRepository;
import TQS.project.backend.service.BookingService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Optional;

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
    public void testCreateBooking_successful() {
        // Arrange
        CreateBookingDTO dto = new CreateBookingDTO();
        dto.setMail("test@example.com");
        dto.setChargerId(1L);
        dto.setStartTime(LocalDateTime.now().plusDays(1));
        dto.setDuration(30);

        Client mockClient = new Client();
        Charger mockCharger = new Charger();

        when(clientRepository.findByMail(dto.getMail())).thenReturn(Optional.of(mockClient));
        when(chargerRepository.findById(dto.getChargerId())).thenReturn(Optional.of(mockCharger));

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
}

