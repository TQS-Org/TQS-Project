package TQS.project.backend;

import TQS.project.backend.entity.Station;
import TQS.project.backend.dto.LoginRequest;
import TQS.project.backend.entity.Client;
import TQS.project.backend.dto.LoginResponse;
import TQS.project.backend.repository.ClientRepository;
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
public class StationIT {

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private ClientRepository clientRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  private String token;

  @BeforeEach
  void setup() {
    // Use Flyway-seeded users or login
    clientRepository.deleteAll(); // optional, but ensures no conflicts

    Client client = new Client();
    client.setMail("driver@mail.com");
    client.setPassword(passwordEncoder.encode("driverpass"));
    client.setName("Driver One");
    client.setAge(30);
    client.setNumber("123456789");
    clientRepository.save(client);

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
    Long testId = 1L; // Assuming station with ID 1 exists from Flyway
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<Void> request = new HttpEntity<>(headers);

    ResponseEntity<Station> response =
        restTemplate.exchange("/api/stations/" + testId, HttpMethod.GET, request, Station.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getId()).isEqualTo(testId);
  }

  @Test
  @Requirement("SCRUM-16")
  void searchStations_withValidFilters_returnsMatchingStations() {
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
}
