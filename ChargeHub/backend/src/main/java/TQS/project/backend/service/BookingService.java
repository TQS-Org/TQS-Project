package TQS.project.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import TQS.project.backend.repository.ClientRepository;
import jakarta.transaction.Transactional;
import TQS.project.backend.dto.CreateBookingDTO;
import TQS.project.backend.entity.Booking;
import TQS.project.backend.entity.Charger;
import TQS.project.backend.entity.Client;
import TQS.project.backend.entity.Station;
import TQS.project.backend.repository.BookingRepository;
import TQS.project.backend.repository.ChargerRepository;

@Service
public class BookingService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm");

    private ClientRepository clientRepository;
    private BookingRepository bookingRepository;
    private ChargerRepository chargerRepository;

    @Autowired
    public BookingService(ClientRepository clientRepository, BookingRepository bookingRepository,
            ChargerRepository chargerRepository) {
        this.clientRepository = clientRepository;
        this.bookingRepository = bookingRepository;
        this.chargerRepository = chargerRepository;
    }

    @Transactional
    public String createBooking(CreateBookingDTO dto) {
        // Validate client exists
        Client user = clientRepository.findByMail(dto.getMail())
                .orElseThrow(() -> new IllegalArgumentException("This email does not exist"));

        // Validate charger exists and get station info
        Charger charger = chargerRepository.findById(dto.getChargerId())
                .orElseThrow(() -> new IllegalArgumentException("This charger does not exist"));

        // Validate booking time against station hours
        validateBookingTime(dto.getStartTime(), dto.getDuration(), charger.getStation());

        // Check for overlapping bookings
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

    public List<Booking> getAllBookingsByDateAndStation(long stationId, LocalDate date) {
        String dateString = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return bookingRepository.findByStationIdAndDate(stationId, dateString);
    }

    public List<Booking> getAllBookingsByStation(long stationId) {
        return bookingRepository.findAllBookingsByStationId(stationId);
    }

    private void validateNoOverlappingBookings(LocalDateTime startTime, int duration, long chargerId) {
        LocalDateTime endTime = startTime.plusMinutes(duration);
        String dateString = startTime.toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        List<Booking> existingBookings = bookingRepository.findByStationIdAndDate(
                chargerRepository.findById(chargerId).get().getStation().getId(),
                dateString);

        for (Booking existing : existingBookings) {
            if (existing.getCharger().getId() == chargerId &&
                    startTime.isBefore(existing.getEndTime()) &&
                    endTime.isAfter(existing.getStartTime())) {
                throw new IllegalArgumentException("The requested time slot overlaps with an existing booking");
            }
        }
    }

}
