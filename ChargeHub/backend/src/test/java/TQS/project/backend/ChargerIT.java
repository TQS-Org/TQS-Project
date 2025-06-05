package TQS.project.backend;

import TQS.project.backend.entity.Station;
import TQS.project.backend.dto.ChargerDTO;
import TQS.project.backend.dto.LoginRequest;
import TQS.project.backend.entity.Booking;
import TQS.project.backend.entity.Charger;
import TQS.project.backend.entity.Client;
import TQS.project.backend.entity.Staff;
import TQS.project.backend.dto.LoginResponse;
import TQS.project.backend.repository.ClientRepository;
import TQS.project.backend.repository.StationRepository;
import TQS.project.backend.entity.Role;
import TQS.project.backend.repository.StaffRepository;
import TQS.project.backend.repository.BookingRepository;
import TQS.project.backend.repository.ChargerRepository;
import TQS.project.backend.repository.ChargingSessionRepository;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
public class ChargerIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private ClientRepository clientRepository;

  @Autowired
  private StationRepository stationRepository;

  @Autowired
  private ChargerRepository chargerRepository;

  @Autowired
  private BookingRepository bookingRepository;

  @Autowired
  private StaffRepository staffRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private ChargingSessionRepository chargingSessionRepository;

  private String token;
  private Long chargerId;

  @BeforeEach
  void setup() {
    chargingSessionRepository.deleteAll();
    bookingRepository.deleteAll();
    clientRepository.deleteAll();
    chargerRepository.deleteAll();
    staffRepository.deleteAll();
    stationRepository.deleteAll();

    Client client = new Client();
    client.setMail("driver@mail.com");
    client.setPassword(passwordEncoder.encode("driverpass"));
    client.setName("Driver One");
    client.setAge(30);
    client.setNumber("123456789");
    clientRepository.save(client);

    // Add at least 4 test stations
    Station savedStation = stationRepository.save(
        new Station(
            "Station A", "BrandX", 38.72, -9.13, "Rua A, Lisboa", 4, "08:00", "20:00", 0.30));

    Charger charger = new Charger("DC", 50.0, true, "CCS");
    charger.setStation(savedStation);
    charger = chargerRepository.save(charger);
    chargerId = charger.getId();

    // Log in
    LoginRequest login = new LoginRequest("driver@mail.com", "driverpass");
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<LoginRequest> request = new HttpEntity<>(login, headers);

    ResponseEntity<LoginResponse> response = restTemplate.postForEntity("/api/auth/login", request,
        LoginResponse.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    token = response.getBody().getToken();
  }

  @Test
  @Requirement("SCRUM-20")
  void whenGetChargerById_thenReturnCharger() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<Charger> response = restTemplate.exchange("/api/charger/" + chargerId, HttpMethod.GET, entity,
        Charger.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Charger charger = response.getBody();
    assertThat(charger).isNotNull();
    assertThat(charger.getType()).isEqualTo("DC");
    assertThat(charger.getPower()).isEqualTo(50.0);
  }

  @Test
  @Requirement("SCRUM-20")
  void whenGetChargerByNonExistingId_thenReturnNotFound() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<String> response = restTemplate.exchange("/api/charger/99999", HttpMethod.GET, entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @Requirement("SCRUM-36")
  void updateCharger_asOperator_succeeds() {
    // Create operator in DB
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
    staffRepository.save(operator);

    // Log in as operator
    LoginRequest login = new LoginRequest("operator@mail.com", "operatorpass");
    HttpHeaders loginHeaders = new HttpHeaders();
    loginHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<LoginRequest> loginRequest = new HttpEntity<>(login, loginHeaders);

    ResponseEntity<LoginResponse> loginResponse = restTemplate.postForEntity("/api/auth/login", loginRequest,
        LoginResponse.class);
    String operatorToken = loginResponse.getBody().getToken();

    // Create a station and a charger
    Station station = new Station(
        "Station Y", "BrandY", 38.70, -9.10, "Rua Y, Lisboa", 4, "08:00", "20:00", 0.40);
    station = stationRepository.save(station);

    Charger charger = new Charger("AC", 22.0, true, "Type2");
    charger.setStation(station);
    charger = chargerRepository.save(charger);

    // Update DTO
    ChargerDTO updateDTO = new ChargerDTO();
    updateDTO.setType("DC");
    updateDTO.setConnectorType("CCS");
    updateDTO.setPower(50.0);
    updateDTO.setAvailable(false);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(operatorToken);
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<ChargerDTO> request = new HttpEntity<>(updateDTO, headers);

    ResponseEntity<Charger> response = restTemplate.exchange(
        "/api/charger/" + charger.getId(), HttpMethod.PUT, request, Charger.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getType()).isEqualTo("DC");
    assertThat(response.getBody().getAvailable()).isFalse();

    // DB validation
    Charger updated = chargerRepository.findById(charger.getId()).orElse(null);
    assertThat(updated).isNotNull();
    assertThat(updated.getType()).isEqualTo("DC");
  }

  @Disabled("Temporarily disabled due to problems with LocalDateTime on CI pipeline reason")
  @Test
  @Requirement("SCRUM-24")
  void whenStartChargingSessionWithValidTokenAndCharger_thenSessionStarts() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    headers.setContentType(MediaType.APPLICATION_JSON);

    LocalDateTime startTime = LocalDateTime.now().minusMinutes(5);
    LocalDateTime endTime = LocalDateTime.now().plusMinutes(30);

    Booking booking = new Booking();
    booking.setToken("BOOKINGTOKEN123");
    booking.setStartTime(startTime);
    booking.setEndTime(endTime);
    booking.setDuration(20);
    booking.setCharger(chargerRepository.findById(chargerId).get());
    booking.setUser(clientRepository.findByMail("driver@mail.com").get());
    bookingRepository.save(booking);

    String json = """
        {
          "chargeToken": "BOOKINGTOKEN123"
        }
        """;

    HttpEntity<String> entity = new HttpEntity<>(json, headers);
    ResponseEntity<String> response = restTemplate.postForEntity("/api/charger/" + chargerId + "/session", entity,
        String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).contains("Charger unlocked successfully");
  }

  @Test
  @Requirement("SCRUM-24")
  void whenStartChargingSessionWithInvalidToken_thenReturnBadRequest() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    headers.setContentType(MediaType.APPLICATION_JSON);

    String json = """
        {
          "chargeToken": "INVALIDTOKEN"
        }
        """;

    HttpEntity<String> entity = new HttpEntity<>(json, headers);
    ResponseEntity<String> response = restTemplate.postForEntity("/api/charger/" + chargerId + "/session", entity,
        String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).contains("No booking found for the given token.");
  }

  @Disabled("Temporarily disabled due to problems with LocalDateTime on CI pipeline reason")
  @Test
  @Requirement("SCRUM-24")
  void whenStartChargingSessionOutsideTimeWindow_thenReturnBadRequest() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    headers.setContentType(MediaType.APPLICATION_JSON);

    LocalDateTime startTime = LocalDateTime.now().plusHours(1);
    LocalDateTime endTime = LocalDateTime.now().plusHours(2);

    Booking booking = new Booking();
    booking.setToken("FUTURETOKEN");
    booking.setStartTime(startTime);
    booking.setEndTime(endTime);
    booking.setDuration(20);
    booking.setCharger(chargerRepository.findById(chargerId).get());
    booking.setUser(clientRepository.findByMail("driver@mail.com").get());
    bookingRepository.save(booking);

    String json = """
        {
          "chargeToken": "FUTURETOKEN"
        }
        """;

    HttpEntity<String> entity = new HttpEntity<>(json, headers);
    ResponseEntity<String> response = restTemplate.postForEntity("/api/charger/" + chargerId + "/session", entity,
        String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).contains("Current time is outside the booking time window.");
  }
}
