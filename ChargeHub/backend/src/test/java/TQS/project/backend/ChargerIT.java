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
public class ChargerIT {

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private ClientRepository clientRepository;

  @Autowired private StationRepository stationRepository;

  @Autowired private ChargerRepository chargerRepository;

  @Autowired private BookingRepository bookingRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  private String token;
  private Long chargerId;

  @BeforeEach
  void setup() {
    bookingRepository.deleteAll();
    clientRepository.deleteAll();
    chargerRepository.deleteAll();
    stationRepository.deleteAll();

    Client client = new Client();
    client.setMail("driver@mail.com");
    client.setPassword(passwordEncoder.encode("driverpass"));
    client.setName("Driver One");
    client.setAge(30);
    client.setNumber("123456789");
    clientRepository.save(client);

    // Add at least 4 test stations
    Station savedStation =
        stationRepository.save(
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

    ResponseEntity<LoginResponse> response =
        restTemplate.postForEntity("/api/auth/login", request, LoginResponse.class);
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

    ResponseEntity<Charger> response =
        restTemplate.exchange("/api/charger/" + chargerId, HttpMethod.GET, entity, Charger.class);

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

    ResponseEntity<String> response =
        restTemplate.exchange("/api/charger/99999", HttpMethod.GET, entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
