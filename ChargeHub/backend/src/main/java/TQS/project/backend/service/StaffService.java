package TQS.project.backend.service;

import TQS.project.backend.dto.AssignStationDTO;
import TQS.project.backend.dto.CreateStaffDTO;
import TQS.project.backend.entity.Role;
import TQS.project.backend.entity.Staff;
import TQS.project.backend.entity.Station;
import TQS.project.backend.repository.StaffRepository;
import TQS.project.backend.repository.StationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@Service
public class StaffService {

  @Autowired private StaffRepository staffRepository;

  @Autowired private StationRepository stationRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  /**
   * Retrieves all staff members with the role of OPERATOR.
   *
   * @return List of Staff members with the role of OPERATOR.
   */
  public List<Staff> getAllOperators() {
    return staffRepository.findByRole(Role.OPERATOR);
  }

  /**
   * Creates a new operator staff member.
   *
   * @param dto Data Transfer Object containing the details of the staff member to be created.
   * @throws IllegalArgumentException if the email is already in use.
   */
  public void createOperator(CreateStaffDTO dto) {
    if (staffRepository.findByMail(dto.getMail()).isPresent()) {
      throw new IllegalArgumentException("Email already in use");
    }

    Staff staff = new Staff();
    staff.setName(dto.getName());
    staff.setMail(dto.getMail());
    staff.setPassword(passwordEncoder.encode(dto.getPassword()));
    staff.setAge(dto.getAge());
    staff.setNumber(dto.getNumber());
    staff.setAddress(dto.getAddress());
    staff.setRole(Role.OPERATOR);
    staff.setActive(true);
    staff.setStartDate(LocalDate.now());

    staffRepository.save(staff);
  }

  /**
   * Assigns a station to an operator.
   *
   * @param dto Data Transfer Object containing the operator's email and station ID.
   * @throws IllegalArgumentException if the operator or station is not found.
   */
  public void assignStationToOperator(AssignStationDTO dto) {
    Staff staff =
        staffRepository
            .findById(dto.getOperatorId())
            .orElseThrow(() -> new RuntimeException("Operator not found."));
    Station station =
        stationRepository
            .findById(dto.getStationId())
            .orElseThrow(() -> new RuntimeException("Station not found."));

    // Check if the station is already assigned to another operator
    Optional<Staff> existingOperator = staffRepository.findByAssignedStationId(station.getId());
    if (existingOperator.isPresent() && !existingOperator.get().getId().equals(staff.getId())) {
      throw new IllegalStateException(
          "This station is already assigned to another operator: "
              + existingOperator.get().getName());
    }

    staff.setAssignedStation(station);
    staffRepository.save(staff);
  }

  public Station getStationForOperator(String email) {
    Optional<Staff> operator = staffRepository.findByMail(email);
    if (operator.isEmpty()) {
      throw new RuntimeException("Operator not found with email: " + email);
    } else {
      Station station = operator.get().getAssignedStation();

      if (station == null) {
        throw new RuntimeException("No station assigned to this operator.");
      }

      return station;
    }
  }
}
