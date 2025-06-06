package TQS.project.backend;

import TQS.project.backend.dto.AssignStationDTO;
import TQS.project.backend.dto.CreateStaffDTO;
import TQS.project.backend.dto.LoginRequest;
import TQS.project.backend.dto.LoginResponse;
import TQS.project.backend.entity.Staff;
import TQS.project.backend.entity.Role;
import TQS.project.backend.entity.Station;
import TQS.project.backend.repository.StaffRepository;
import TQS.project.backend.repository.BookingRepository;
import TQS.project.backend.repository.ChargingSessionRepository;
import TQS.project.backend.repository.ClientRepository;
import TQS.project.backend.repository.ChargerRepository;
import TQS.project.backend.repository.StationRepository;
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

  @Autowired private StationRepository stationRepository;

  @Autowired private ChargerRepository chargerRepository;

  @Autowired private BookingRepository bookingRepository;

  @Autowired private ChargingSessionRepository chargingSessionRepository;

  @Autowired private ClientRepository clientRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  private String token;

  @BeforeEach
  void setup() {
    // Optional cleanup
    chargingSessionRepository.deleteAll();
    bookingRepository.deleteAll();
    clientRepository.deleteAll();
    chargerRepository.deleteAll();
    staffRepository.deleteAll();
    stationRepository.deleteAll();

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
        restTemplate.exchange("/api/staff/operators", HttpMethod.GET, request, Staff[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotEmpty();
    assertThat(response.getBody()[0].getMail()).isEqualTo("operator@mail.com");
  }

  @Test
  @Requirement("SCRUM-36")
  void assignStationToOperator_asAdmin_succeeds() {

    // Create operator
    Staff operator = new Staff();
    operator.setMail("operator@mail.com");
    operator.setPassword(passwordEncoder.encode("operatorpass"));
    operator.setName("Operator");
    operator.setAge(30);
    operator.setNumber("911111111");
    operator.setAddress("Porto");
    operator.setActive(true);
    operator.setRole(Role.OPERATOR);
    operator.setStartDate(java.time.LocalDate.now());
    operator = staffRepository.save(operator);

    // Create station
    Station station =
        new Station(
            "Station Z", "BrandZ", 38.72, -9.13, "Rua Z, Lisboa", 4, "08:00", "20:00", 0.30);
    station = stationRepository.save(station);

    // Assign station to operator
    AssignStationDTO dto = new AssignStationDTO();
    dto.setOperatorId(operator.getId());
    dto.setStationId(station.getId());

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<AssignStationDTO> request = new HttpEntity<>(dto, headers);

    ResponseEntity<String> response =
        restTemplate.postForEntity("/api/staff/operator/assign-station", request, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).contains("Station assigned to operator successfully.");

    // DB validation
    Staff updatedOperator = staffRepository.findById(operator.getId()).orElse(null);
    assertThat(updatedOperator).isNotNull();
    assertThat(updatedOperator.getAssignedStation().getId()).isEqualTo(station.getId());
  }

  @Test
  @Requirement("SCRUM-34")
  void getStationForOperator_asOperator_succeeds() {
    // Create operator
    Staff operator = new Staff();
    operator.setMail("operator@mail.com");
    operator.setPassword(passwordEncoder.encode("op123"));
    operator.setName("Operator X");
    operator.setAge(32);
    operator.setNumber("911111111");
    operator.setAddress("Faro");
    operator.setActive(true);
    operator.setRole(Role.OPERATOR);
    operator.setStartDate(java.time.LocalDate.now());

    // Create station and assign
    Station station =
        new Station("Station X", "BrandX", 40.7, -8.6, "Rua X, Aveiro", 5, "07:00", "23:00", 0.28);
    station = stationRepository.save(station);
    operator.setAssignedStation(station);
    staffRepository.save(operator);

    // Login as operator
    LoginRequest loginRequest = new LoginRequest("operator@mail.com", "op123");
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<LoginRequest> loginEntity = new HttpEntity<>(loginRequest, headers);

    ResponseEntity<LoginResponse> loginResponse =
        restTemplate.postForEntity("/api/auth/login", loginEntity, LoginResponse.class);

    assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    String operatorToken = loginResponse.getBody().getToken();

    // Make GET request to retrieve the assigned station
    HttpHeaders authHeaders = new HttpHeaders();
    authHeaders.setBearerAuth(operatorToken);
    HttpEntity<Void> request = new HttpEntity<>(authHeaders);

    ResponseEntity<Station> response =
        restTemplate.exchange("/api/staff/station", HttpMethod.GET, request, Station.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Station returnedStation = response.getBody();
    assertThat(returnedStation).isNotNull();
    assertThat(returnedStation.getId()).isEqualTo(station.getId());
    assertThat(returnedStation.getName()).isEqualTo("Station X");
    assertThat(returnedStation.getAddress()).isEqualTo("Rua X, Aveiro");
  }
}
