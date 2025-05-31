package TQS.project.backend.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;

import TQS.project.backend.repository.ClientRepository;
import TQS.project.backend.dto.CreateBookingDTO;
import TQS.project.backend.entity.Booking;
import TQS.project.backend.repository.BookingRepository;

public class BookingService {
    
    private ClientRepository clientRepository;
    private BookingRepository bookingRepository;

    @Autowired
    public BookingService(ClientRepository clientRepository, BookingRepository bookingRepository){
        this.clientRepository = clientRepository;
        this.bookingRepository = bookingRepository;
    }

    /**
   * Creates a new operator staff member.
   *
   * @param dto Data Transfer Object containing the details of the staff member to be created.
   * @throws IllegalArgumentException if the email is already in use.
   */
  public void createBooking(CreateBookingDTO dto) {
    //if (!clientRepository.findByMail(dto.getMail()).isPresent()) {
    //  throw new IllegalArgumentException("This email does not exist");
    //}
//
    //Booking booking;
    //booking.setName(dto.getName());
    //booking.setMail(dto.getMail());
    //booking.setPassword(passwordEncoder.encode(dto.getPassword()));
    //booking.setAge(dto.getAge());
    //booking.setNumber(dto.getNumber());
    //booking.setAddress(dto.getAddress());
    //booking.setRole(Role.OPERATOR);
    //booking.setActive(true);
    //booking.setStartDate(LocalDate.now());
//
    //staffRepository.save(staff);
  }
}
