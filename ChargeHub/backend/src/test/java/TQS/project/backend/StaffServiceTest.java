package TQS.project.backend;

import TQS.project.backend.dto.CreateStaffDTO;
import TQS.project.backend.entity.Role;
import TQS.project.backend.entity.Staff;
import TQS.project.backend.repository.StaffRepository;
import TQS.project.backend.repository.StationRepository;
import TQS.project.backend.service.StaffService;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class StaffServiceTest {

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private StationRepository stationRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private StaffService staffService;

    @Test
    @Requirement("SCRUM-35")
    void testCreateOperator_success() {
        CreateStaffDTO dto = new CreateStaffDTO();
        dto.name = "Operator";
        dto.mail = "operator@mail.com";
        dto.password = "plainpass";
        dto.age = 30;
        dto.number = "123456789";
        dto.address = "Lisboa";

        when(staffRepository.findByMail(dto.mail)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dto.password)).thenReturn("encodedpass");

        staffService.createOperator(dto);

        verify(staffRepository, times(1)).save(argThat(staff -> staff.getName().equals(dto.name) &&
                staff.getMail().equals(dto.mail) &&
                staff.getPassword().equals("encodedpass") &&
                staff.getRole() == Role.OPERATOR &&
                staff.getStartDate().equals(LocalDate.now())));
    }

    @Test
    @Requirement("SCRUM-35")
    void testCreateOperator_duplicateEmail_throwsException() {
        CreateStaffDTO dto = new CreateStaffDTO();
        dto.mail = "duplicate@mail.com";

        when(staffRepository.findByMail(dto.mail)).thenReturn(Optional.of(new Staff()));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            staffService.createOperator(dto);
        });

        assertEquals("Email already in use", thrown.getMessage());
        verify(staffRepository, never()).save(any());
    }

    @Test
    @Requirement("SCRUM-35")
    void testGetAllOperators_returnsOperatorsOnly() {
        Staff s1 = new Staff();
        s1.setName("Operator One");
        s1.setRole(Role.OPERATOR);

        Staff s2 = new Staff();
        s2.setName("Operator Two");
        s2.setRole(Role.OPERATOR);

        when(staffRepository.findByRole(Role.OPERATOR)).thenReturn(List.of(s1, s2));

        var result = staffService.getAllOperators();

        assertEquals(2, result.size());
        assertEquals("Operator One", result.get(0).getName());
    }

}
