package TQS.project.backend;

import TQS.project.backend.controller.StaffController;
import TQS.project.backend.dto.AssignStationDTO;
import TQS.project.backend.dto.CreateStaffDTO;
import TQS.project.backend.entity.Staff;
import TQS.project.backend.entity.Station;
import TQS.project.backend.security.JwtAuthFilter;
import TQS.project.backend.security.JwtProvider;
import TQS.project.backend.service.StaffService;
import TQS.project.backend.entity.Role;
import java.util.List;
import TQS.project.backend.Config.TestSecurityConfig;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestSecurityConfig.class)
@WebMvcTest(StaffController.class)
@AutoConfigureMockMvc(addFilters = false)
public class StaffControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @SuppressWarnings("removal")
  @MockBean
  private StaffService staffService;

  @SuppressWarnings("removal")
  @MockBean
  private JwtProvider jwtProvider;

  @SuppressWarnings("removal")
  @MockBean
  private JwtAuthFilter jwtAuthFilter;

  @Test
  @Requirement("SCRUM-35")
  void testCreateOperator_success() throws Exception {
    CreateStaffDTO dto = new CreateStaffDTO();
    dto.setName("New Operator");
    dto.setMail("newoperator@mail.com");
    dto.setPassword("secure123");
    dto.setAge(30);
    dto.setNumber("912888777");
    dto.setAddress("Setúbal");

    doNothing().when(staffService).createOperator(dto);

    mockMvc
        .perform(
            post("/api/staff/operator")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(content().string("Operator account created successfully."));
  }

  @Test
  @Requirement("SCRUM-35")
  void testCreateOperator_duplicateEmail() throws Exception {
    CreateStaffDTO dto = new CreateStaffDTO();
    dto.setName("Duplicate");
    dto.setMail("existing@mail.com");
    dto.setPassword("securepass123");
    dto.setAge(25);
    dto.setNumber("912456789");
    dto.setAddress("Lisbon");

    doThrow(new IllegalArgumentException("Email already in use"))
        .when(staffService)
        .createOperator(any(CreateStaffDTO.class));

    mockMvc
        .perform(
            post("/api/staff/operator")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Email already in use"));
  }

  @Test
  @Requirement("SCRUM-35")
  void testGetAllOperators_returnsList() throws Exception {
    Staff s1 = new Staff();
    s1.setId(1L);
    s1.setName("Operator One");
    s1.setMail("op1@mail.com");
    s1.setRole(Role.OPERATOR);

    Staff s2 = new Staff();
    s2.setId(2L);
    s2.setName("Operator Two");
    s2.setMail("op2@mail.com");
    s2.setRole(Role.OPERATOR);

    when(staffService.getAllOperators()).thenReturn(List.of(s1, s2));

    mockMvc
        .perform(get("/api/staff/operators"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].name").value("Operator One"))
        .andExpect(jsonPath("$[1].mail").value("op2@mail.com"));
  }

  @Test
  @Requirement("SCRUM-35")
  void testCreateOperator_validationFails() throws Exception {
    // Create a DTO with invalid data
    CreateStaffDTO dto = new CreateStaffDTO();
    dto.setName(""); // Invalid: name is blank
    dto.setMail("invalid-email"); // Invalid email format
    dto.setPassword("short"); // Invalid: too short, no number
    dto.setAge(17); // Invalid: less than 18
    dto.setNumber("12345"); // Invalid phone number
    dto.setAddress(""); // Invalid: blank

    mockMvc
        .perform(
            post("/api/staff/operator")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.name").value("Name is required"))
        .andExpect(jsonPath("$.age").value("Age must be at least 18"))
        .andExpect(
            jsonPath("$.number").value("Phone number must start with 9 and be exactly 9 digits"))
        .andExpect(jsonPath("$.mail").value("Email should be valid"))
        .andExpect(jsonPath("$.address").value("Address is required"));
  }

  @Test
  @Requirement("SCRUM-36")
  void testAssignStationToOperator_success() throws Exception {
    AssignStationDTO dto = new AssignStationDTO();
    dto.setOperatorId(1L);
    dto.setStationId(100L);

    doNothing().when(staffService).assignStationToOperator(dto);

    mockMvc
        .perform(
            post("/api/staff/operator/assign-station")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(content().string("Station assigned to operator successfully."));
  }

  @Test
  @Requirement("SCRUM-36")
  void testAssignStationToOperator_notFound() throws Exception {
    AssignStationDTO dto = new AssignStationDTO();
    dto.setOperatorId(999L);
    dto.setStationId(100L);

    doThrow(new RuntimeException("Staff or Station not found."))
        .when(staffService)
        .assignStationToOperator(any(AssignStationDTO.class));

    mockMvc
        .perform(
            post("/api/staff/operator/assign-station")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Staff or Station not found."));
  }

  @Test
  @Requirement("SCRUM-36")
  void testAssignStationToOperator_validationFails() throws Exception {
    AssignStationDTO dto = new AssignStationDTO();
    // Missing required fields

    mockMvc
        .perform(
            post("/api/staff/operator/assign-station")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @Requirement("SCRUM-34")
  void testGetMyStation_success() throws Exception {
    Station station = new Station();
    station.setId(100L);
    station.setName("My Station");

    when(jwtProvider.getEmailFromToken("testtoken")).thenReturn("operator@mail.com");
    when(staffService.getStationForOperator("operator@mail.com")).thenReturn(station);

    mockMvc
        .perform(get("/api/staff/station").header("Authorization", "Bearer testtoken"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(100L))
        .andExpect(jsonPath("$.name").value("My Station"));
  }

  @Test
  @Requirement("SCRUM-34")
  void testGetMyStation_noStationAssigned() throws Exception {
    when(jwtProvider.getEmailFromToken("testtoken")).thenReturn("operator@mail.com");
    when(staffService.getStationForOperator("operator@mail.com"))
        .thenThrow(new RuntimeException("No station assigned to this operator."));

    mockMvc
        .perform(get("/api/staff/station").header("Authorization", "Bearer testtoken"))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("No station assigned to this operator."));
  }
}
