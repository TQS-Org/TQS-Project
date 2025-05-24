package TQS.project.backend;

import TQS.project.backend.dto.CreateStaffDTO;
import TQS.project.backend.dto.LoginRequest;
import TQS.project.backend.dto.LoginResponse;
import TQS.project.backend.entity.Staff;
import TQS.project.backend.repository.StaffRepository;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
public class StaffIT {

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private StaffRepository staffRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  private String token;

  @BeforeEach
  void setup() {
    // Optional cleanup
    staffRepository.deleteAll();

    // Insert admin manually
    Staff admin = new Staff();
    admin.setMail("admin@mail.com");
    admin.setPassword(passwordEncoder.encode("adminpass"));
    admin.setName("Admin One");
    admin.setAge(40);
    admin.setNumber("999999999");
    admin.setAddress("Santarém");
    admin.setActive(true);
    admin.setRole(TQS.project.backend.entity.Role.ADMIN);
    admin.setStartDate(java.time.LocalDate.now());
    staffRepository.save(admin);

    // Login as admin
    LoginRequest login = new LoginRequest("admin@mail.com", "adminpass");
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<LoginRequest> request = new HttpEntity<>(login, headers);

    ResponseEntity<LoginResponse> response =
        restTemplate.postForEntity("/api/auth/login", request, LoginResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    token = response.getBody().getToken();
  }

  @Test
  @Requirement("SCRUM-35")
  void createOperator_asAdmin_succeeds() {
    CreateStaffDTO dto = new CreateStaffDTO();
    dto.setName("New Operator");
    dto.setMail("newoperator@mail.com");
    dto.setPassword("securepass123");
    dto.setAge(33);
    dto.setNumber("912123123");
    dto.setAddress("Porto");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(token);

    HttpEntity<CreateStaffDTO> request = new HttpEntity<>(dto, headers);

    ResponseEntity<String> response =
        restTemplate.postForEntity("/api/staff/operator", request, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).contains("Operator account created successfully.");

    Staff saved = staffRepository.findByMail("newoperator@mail.com").orElse(null);
    assertThat(saved).isNotNull();
    assertThat(saved.getName()).isEqualTo("New Operator");
    assertThat(saved.getRole().name()).isEqualTo("OPERATOR");
  }

  @Test
  @Requirement("SCRUM-35")
  void createOperator_duplicateEmail_returns400() {
    // reuse admin's email to trigger conflict
    CreateStaffDTO dto = new CreateStaffDTO();
    dto.setName("Duplicate");
    dto.setMail("admin@mail.com");
    dto.setPassword("securepass123");
    dto.setAge(40);
    dto.setNumber("912999999");
    dto.setAddress("Santarém");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(token);

    HttpEntity<CreateStaffDTO> request = new HttpEntity<>(dto, headers);

    ResponseEntity<String> response =
        restTemplate.postForEntity("/api/staff/operator", request, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).contains("Email already in use");
  }

  @Test
  @Requirement("SCRUM-35")
  void getAllOperators_returnsList() {
    Staff op = new Staff();
    op.setMail("operator@mail.com");
    op.setPassword(passwordEncoder.encode("pass"));
    op.setName("Operator");
    op.setAge(33);
    op.setNumber("111111111");
    op.setAddress("Lisboa");
    op.setActive(true);
    op.setRole(TQS.project.backend.entity.Role.OPERATOR);
    op.setStartDate(java.time.LocalDate.now());
    staffRepository.save(op);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<Void> request = new HttpEntity<>(headers);

    ResponseEntity<Staff[]> response =
        restTemplate.exchange(
            "/api/staff/operators", HttpMethod.GET, request, Staff[].class, request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotEmpty();
    assertThat(response.getBody()[0].getMail()).isEqualTo("operator@mail.com");
  }
}
