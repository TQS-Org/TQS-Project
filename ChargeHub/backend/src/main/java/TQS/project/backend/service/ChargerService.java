package TQS.project.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

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
public class ChargerService {

    private ChargerRepository chargerRepository;

    @Autowired
    public ChargerService(ChargerRepository chargerRepository) {
        this.chargerRepository = chargerRepository;
    }

    public Optional<Charger> getChargerById(Long id) {
        return chargerRepository.findById(id);
    }
}
