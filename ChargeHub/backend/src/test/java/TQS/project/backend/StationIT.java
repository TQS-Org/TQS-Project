package TQS.project.backend;

import TQS.project.backend.entity.Station;
import TQS.project.backend.dto.LoginRequest;
import TQS.project.backend.entity.Charger;
import TQS.project.backend.entity.Client;
import TQS.project.backend.entity.Staff;
import TQS.project.backend.dto.LoginResponse;
import TQS.project.backend.dto.StationDTO;
import TQS.project.backend.entity.Role;
import TQS.project.backend.repository.ClientRepository;
import TQS.project.backend.repository.StationRepository;
import TQS.project.backend.repository.BookingRepository;
import TQS.project.backend.repository.ChargerRepository;
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

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
public class StationIT {

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private ClientRepository clientRepository;

  @Autowired private BookingRepository bookingRepository;

  @Autowired private StationRepository stationRepository;

  @Autowired private ChargerRepository chargerRepository;

  @Autowired private ChargerRepository chargingSessionRepository;

  @Autowired private StaffRepository staffRepository; // Assuming you have a StaffRepository

  @Autowired private PasswordEncoder passwordEncoder;

  private String token;

  @BeforeEach
  void setup() {
    chargingSessionRepository.deleteAllInBatch();
    bookingRepository.deleteAll();
    clientRepository.deleteAll();
    chargerRepository.deleteAll(); // <--- delete chargers before stations
    staffRepository.deleteAll(); // Assuming you have a StaffRepository
    stationRepository.deleteAll();

    Client client = new Client();
    client.setMail("driver@mail.com");
    client.setPassword(passwordEncoder.encode("driverpass"));
    client.setName("Driver One");
    client.setAge(30);
    client.setNumber("123456789");
    clientRepository.save(client);

    // Add at least 4 test stations
    stationRepository.save(
        new Station(
            "Station A", "BrandX", 38.72, -9.13, "Rua A, Lisboa", 4, "08:00", "20:00", 0.30));
    stationRepository.save(
        new Station(
            "Station B", "BrandY", 38.73, -9.12, "Rua B, Lisboa", 3, "07:00", "21:00", 0.32));
    stationRepository.save(
        new Station(
            "Station C", "BrandZ", 38.74, -9.11, "Rua C, Lisboa", 5, "06:00", "22:00", 0.34));
    stationRepository.save(
        new Station(
            "Station D", "BrandW", 38.75, -9.10, "Rua D, Lisboa", 2, "09:00", "19:00", 0.33));

    // Log in
    LoginRequest login = new LoginRequest("driver@mail.com", "driverpass");
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
  @Requirement("SCRUM-16")
  void getAllStations_returnsStationList() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<Void> request = new HttpEntity<>(headers);

    ResponseEntity<Station[]> response =
        restTemplate.exchange("/api/stations", HttpMethod.GET, request, Station[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotEmpty();
    assertThat(response.getBody().length).isGreaterThan(3); // Based on Flyway seed
  }

  @Test
  @Requirement("SCRUM-16")
  void getStationById_returnsCorrectStation() {
    Station station = stationRepository.findAll().get(0); // Get any one of the saved stations

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<Void> request = new HttpEntity<>(headers);

    ResponseEntity<Station> response =
        restTemplate.exchange(
            "/api/stations/" + station.getId(), HttpMethod.GET, request, Station.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getId()).isEqualTo(station.getId());
  }

  @Test
  @Requirement("SCRUM-16")
  void searchStations_withValidFilters_returnsMatchingStations() {
    Station matchingStation =
        new Station(
            "Matching Station",
            "BrandMatch",
            38.76,
            -9.14,
            "Rua Z, Lisboa",
            3,
            "08:00",
            "20:00",
            0.30);
    matchingStation = stationRepository.save(matchingStation);

    Charger matchingCharger = new Charger();
    matchingCharger.setStation(matchingStation);
    matchingCharger.setType("DC");
    matchingCharger.setPower(100); // in range 50–150
    matchingCharger.setConnectorType("CCS");
    matchingCharger.setAvailable(true);
    chargerRepository.save(matchingCharger);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<Void> request = new HttpEntity<>(headers);

    String url =
        "/api/stations/search?district=Lisboa&maxPrice=0.35&chargerType=DC&minPower=50&maxPower=150&connectorType=CCS&available=true";

    ResponseEntity<Station[]> response =
        restTemplate.exchange(url, HttpMethod.GET, request, Station[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotEmpty();
    assertThat(response.getBody()[0].getAddress()).containsIgnoringCase("Lisboa");
  }

  @Test
  @Requirement("SCRUM-16")
  void getStationById_notFound_returns404() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<Void> request = new HttpEntity<>(headers);

    ResponseEntity<String> response =
        restTemplate.exchange("/api/stations/9999", HttpMethod.GET, request, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @Requirement("SCRUM-20")
  void getStationChargers_returnsChargersForStation() {
    // Create a station
    Station station =
        new Station(
            "Charger Test Station",
            "BrandTest",
            38.80,
            -9.15,
            "Rua Charger, Lisboa",
            3,
            "07:00",
            "23:00",
            0.28);
    station = stationRepository.save(station);

    // Create two chargers for this station
    Charger charger1 = new Charger();
    charger1.setStation(station);
    charger1.setType("AC");
    charger1.setConnectorType("Type2");
    charger1.setPower(22.0);
    charger1.setAvailable(true);

    Charger charger2 = new Charger();
    charger2.setStation(station);
    charger2.setType("DC");
    charger2.setConnectorType("CCS");
    charger2.setPower(50.0);
    charger2.setAvailable(true);

    chargerRepository.saveAll(List.of(charger1, charger2));

    // Prepare headers with authentication token
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<Void> request = new HttpEntity<>(headers);

    // Send request
    ResponseEntity<Charger[]> response =
        restTemplate.exchange(
            "/api/stations/" + station.getId() + "/chargers",
            HttpMethod.GET,
            request,
            Charger[].class);

    // Assert response
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().length).isEqualTo(2);
    assertThat(response.getBody()[0].getType()).isIn("AC", "DC");
    assertThat(response.getBody()[1].getType()).isIn("AC", "DC");
  }

  @Test
  @Requirement("SCRUM-36")
  void createStation_asAdmin_succeeds() {
    // Create admin in DB
    Staff admin = new Staff();
    admin.setMail("admin@mail.com");
    admin.setPassword(passwordEncoder.encode("adminpass"));
    admin.setName("Admin One");
    admin.setAge(40);
    admin.setNumber("999999999");
    admin.setAddress("Santarém");
    admin.setActive(true);
    admin.setRole(Role.ADMIN);
    admin.setStartDate(java.time.LocalDate.now());
    staffRepository.save(admin);

    // Log in as admin
    LoginRequest login = new LoginRequest("admin@mail.com", "adminpass");
    HttpHeaders loginHeaders = new HttpHeaders();
    loginHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<LoginRequest> loginRequest = new HttpEntity<>(login, loginHeaders);

    ResponseEntity<LoginResponse> loginResponse =
        restTemplate.postForEntity("/api/auth/login", loginRequest, LoginResponse.class);
    assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

    String adminToken = loginResponse.getBody().getToken();

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(adminToken);
    headers.setContentType(MediaType.APPLICATION_JSON);

    StationDTO stationDTO = new StationDTO();
    stationDTO.setName("New Station");
    stationDTO.setBrand("BrandNew");
    stationDTO.setLatitude(38.80);
    stationDTO.setLongitude(-9.10);
    stationDTO.setAddress("Rua Nova, Lisboa");
    stationDTO.setNumberOfChargers(5);
    stationDTO.setOpeningHours("06:00");
    stationDTO.setClosingHours("22:00");
    stationDTO.setPrice(0.25);

    HttpEntity<StationDTO> request = new HttpEntity<>(stationDTO, headers);

    ResponseEntity<Station> response =
        restTemplate.postForEntity("/api/stations", request, Station.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getName()).isEqualTo("New Station");
  }

  @Test
  @Requirement("SCRUM-36")
  void updateStation_asOperator_succeeds() {
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

    ResponseEntity<LoginResponse> loginResponse =
        restTemplate.postForEntity("/api/auth/login", loginRequest, LoginResponse.class);
    assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

    String operatorToken = loginResponse.getBody().getToken();

    // Create a station to update
    Station station =
        new Station(
            "Old Station", "BrandOld", 38.70, -9.11, "Old Rua, Lisboa", 3, "07:00", "21:00", 0.35);
    station = stationRepository.save(station);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(operatorToken);
    headers.setContentType(MediaType.APPLICATION_JSON);

    StationDTO updateDTO = new StationDTO();
    updateDTO.setName("Updated Station");
    updateDTO.setBrand("BrandUpdated");
    updateDTO.setAddress("Updated Rua, Lisboa");
    updateDTO.setLatitude(38.75);
    updateDTO.setLongitude(-9.14);
    updateDTO.setPrice(0.30);
    updateDTO.setOpeningHours("08:00");
    updateDTO.setClosingHours("20:00");

    HttpEntity<StationDTO> request = new HttpEntity<>(updateDTO, headers);

    ResponseEntity<Station> response =
        restTemplate.exchange(
            "/api/stations/" + station.getId(), HttpMethod.PUT, request, Station.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getName()).isEqualTo("Updated Station");
  }
}
