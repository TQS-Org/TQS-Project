package TQS.project.backend.service;

import TQS.project.backend.dto.CreateStaffDTO;
import TQS.project.backend.entity.Role;
import TQS.project.backend.entity.Staff;
import TQS.project.backend.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

import java.time.LocalDate;

@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
     * @param dto Data Transfer Object containing the details of the staff member to
     *            be created.
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
}
