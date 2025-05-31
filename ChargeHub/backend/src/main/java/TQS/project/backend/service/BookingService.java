package TQS.project.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import TQS.project.backend.repository.ClientRepository;
import jakarta.transaction.Transactional;
import TQS.project.backend.dto.CreateBookingDTO;
import TQS.project.backend.entity.Booking;
import TQS.project.backend.entity.Charger;
import TQS.project.backend.entity.Client;
import TQS.project.backend.repository.BookingRepository;
import TQS.project.backend.repository.ChargerRepository;

@Service
public class BookingService {
    
    private ClientRepository clientRepository;
    private BookingRepository bookingRepository;
    private ChargerRepository chargerRepository;

    @Autowired
    public BookingService(ClientRepository clientRepository, BookingRepository bookingRepository, ChargerRepository chargerRepository){
        this.clientRepository = clientRepository;
        this.bookingRepository = bookingRepository;
        this.chargerRepository = chargerRepository;
    }

    public String createBooking(CreateBookingDTO dto) {
        Client user = clientRepository.findByMail(dto.getMail())
                .orElseThrow(() -> new IllegalArgumentException("This email does not exist"));

        Charger charger = chargerRepository.findById(dto.getChargerId())
                .orElseThrow(() -> new IllegalArgumentException("This charger does not exist"));

        Booking booking = new Booking(user, charger, dto.getStartTime(), dto.getDuration());

        bookingRepository.save(booking);
        return booking.getToken(); // return token as booking reference
    }
}
