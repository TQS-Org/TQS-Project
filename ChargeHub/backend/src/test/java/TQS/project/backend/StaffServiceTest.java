package TQS.project.backend;

import TQS.project.backend.dto.AssignStationDTO;
import TQS.project.backend.dto.CreateStaffDTO;
import TQS.project.backend.entity.Role;
import TQS.project.backend.entity.Staff;
import TQS.project.backend.entity.Station;
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

  @Mock private StaffRepository staffRepository;

  @Mock private StationRepository stationRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private StaffService staffService;

  @Test
  @Requirement("SCRUM-35")
  void testCreateOperator_success() {
    CreateStaffDTO dto = new CreateStaffDTO();
    dto.setName("New Operator");
    dto.setMail("newoperator@mail.com");
    dto.setPassword("secure123");
    dto.setAge(30);
    dto.setNumber("912345678");
    dto.setAddress("Setúbal");

    when(staffRepository.findByMail(dto.getMail())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedpass");

    staffService.createOperator(dto);

    verify(staffRepository, times(1))
        .save(
            argThat(
                staff ->
                    staff.getName().equals(dto.getName())
                        && staff.getMail().equals(dto.getMail())
                        && staff.getPassword().equals("encodedpass")
                        && staff.getRole() == Role.OPERATOR
                        && staff.getStartDate().equals(LocalDate.now())));
  }

  @Test
  @Requirement("SCRUM-35")
  void testCreateOperator_duplicateEmail_throwsException() {
    CreateStaffDTO dto = new CreateStaffDTO();
    dto.setMail("duplicate@mail.com");

    when(staffRepository.findByMail(dto.getMail())).thenReturn(Optional.of(new Staff()));

    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
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

  @Test
  @Requirement("SCRUM-36")
  void testAssignStationToOperator_success() {
    AssignStationDTO dto = new AssignStationDTO();
    dto.setOperatorId(1L);
    dto.setStationId(100L);

    Staff staff = new Staff();
    staff.setId(1L);

    Station station = new Station();
    station.setId(100L);

    when(staffRepository.findById(1L)).thenReturn(Optional.of(staff));
    when(stationRepository.findById(100L)).thenReturn(Optional.of(station));
    when(staffRepository.findByAssignedStationId(100L)).thenReturn(Optional.empty());

    staffService.assignStationToOperator(dto);

    verify(staffRepository, times(1))
        .save(argThat(savedStaff -> savedStaff.getAssignedStation().equals(station)));
  }

  @Test
  @Requirement("SCRUM-36")
  void testAssignStationToOperator_operatorNotFound() {
    AssignStationDTO dto = new AssignStationDTO();
    dto.setOperatorId(1L);
    dto.setStationId(100L);

    when(staffRepository.findById(1L)).thenReturn(Optional.empty());

    RuntimeException thrown =
        assertThrows(RuntimeException.class, () -> staffService.assignStationToOperator(dto));

    assertEquals("Operator not found.", thrown.getMessage());
    verify(staffRepository, never()).save(any());
  }

  @Test
  @Requirement("SCRUM-36")
  void testAssignStationToOperator_stationNotFound() {
    AssignStationDTO dto = new AssignStationDTO();
    dto.setOperatorId(1L);
    dto.setStationId(100L);

    Staff staff = new Staff();
    staff.setId(1L);

    when(staffRepository.findById(1L)).thenReturn(Optional.of(staff));
    when(stationRepository.findById(100L)).thenReturn(Optional.empty());

    RuntimeException thrown =
        assertThrows(RuntimeException.class, () -> staffService.assignStationToOperator(dto));

    assertEquals("Station not found.", thrown.getMessage());
    verify(staffRepository, never()).save(any());
  }

  @Test
  @Requirement("SCRUM-36")
  void testAssignStationToOperator_stationAlreadyAssigned_throwsException() {
    AssignStationDTO dto = new AssignStationDTO();
    dto.setOperatorId(1L);
    dto.setStationId(100L);

    Staff staff1 = new Staff();
    staff1.setId(1L);

    Staff staff2 = new Staff();
    staff2.setId(2L);
    staff2.setName("Other Operator");

    Station station = new Station();
    station.setId(100L);

    when(staffRepository.findById(1L)).thenReturn(Optional.of(staff1));
    when(stationRepository.findById(100L)).thenReturn(Optional.of(station));
    when(staffRepository.findByAssignedStationId(100L)).thenReturn(Optional.of(staff2));

    IllegalStateException thrown =
        assertThrows(IllegalStateException.class, () -> staffService.assignStationToOperator(dto));

    assertTrue(thrown.getMessage().contains("already assigned to another operator"));
    verify(staffRepository, never()).save(any());
  }

  @Test
  @Requirement("SCRUM-34")
  void testGetStationForOperator_success() {
    String email = "operator@mail.com";

    Station station = new Station();
    station.setId(100L);

    Staff staff = new Staff();
    staff.setMail(email);
    staff.setAssignedStation(station);

    when(staffRepository.findByMail(email)).thenReturn(Optional.of(staff));

    Station result = staffService.getStationForOperator(email);

    assertNotNull(result);
    assertEquals(100L, result.getId());
  }

  @Test
  @Requirement("SCRUM-34")
  void testGetStationForOperator_noOperatorFound() {
    String email = "missing@mail.com";
    when(staffRepository.findByMail(email)).thenReturn(Optional.empty());

    RuntimeException thrown =
        assertThrows(RuntimeException.class, () -> staffService.getStationForOperator(email));

    assertEquals("Operator not found with email: " + email, thrown.getMessage());
  }

  @Test
  @Requirement("SCRUM-34")
  void testGetStationForOperator_noStationAssigned() {
    String email = "operator@mail.com";

    Staff staff = new Staff();
    staff.setMail(email);
    staff.setAssignedStation(null);

    when(staffRepository.findByMail(email)).thenReturn(Optional.of(staff));

    RuntimeException thrown =
        assertThrows(RuntimeException.class, () -> staffService.getStationForOperator(email));

    assertEquals("No station assigned to this operator.", thrown.getMessage());
  }
}
