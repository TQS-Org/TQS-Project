package TQS.project.backend;

import TQS.project.backend.entity.Station;
import TQS.project.backend.dto.LoginRequest;
import TQS.project.backend.entity.Charger;
import TQS.project.backend.entity.Client;
import TQS.project.backend.dto.LoginResponse;
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

  @Autowired private StaffRepository staffRepository; // Assuming you have a StaffRepository

  @Autowired private PasswordEncoder passwordEncoder;

  private String token;

  @BeforeEach
  void setup() {
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
}
