package TQS.project.backend;

import TQS.project.backend.entity.Client;
import TQS.project.backend.repository.ClientRepository;
import TQS.project.backend.repository.BookingRepository;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import TQS.project.backend.dto.*;
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
public class AuthIT {

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private ClientRepository clientRepository;

  @Autowired private BookingRepository bookingRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setup() {
    bookingRepository.deleteAll();
    clientRepository.deleteAll();
    Client client = new Client();
    client.setName("Alice");
    client.setMail("alice@example.com");
    client.setPassword(passwordEncoder.encode("plainpass")); // make sure this matches the encoder
    clientRepository.save(client);
  }

  @Test
  @Requirement("SCRUM-41")
  void testLoginSuccess() {
    var loginPayload =
        new LoginRequest("alice@example.com", "plainpass"); // password depends on your encoder

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<LoginRequest> request = new HttpEntity<>(loginPayload, headers);

    ResponseEntity<String> response =
        restTemplate.postForEntity("/api/auth/login", request, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).contains("token");
  }

  @Test
  @Requirement("SCRUM-147")
  void testRegisterNewClient_returnsTokenAndPersists() {
    RegisterRequest request = new RegisterRequest();
    request.setName("Bob");
    request.setPassword("bobpass123");
    request.setAge(35);
    request.setMail("bob@mail.com");
    request.setNumber("919191919");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<RegisterRequest> entity = new HttpEntity<>(request, headers);

    ResponseEntity<LoginResponse> response =
        restTemplate.postForEntity("/api/auth/register", entity, LoginResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getToken()).isNotBlank();
    assertThat(response.getBody().getRole()).isEqualTo("EV_DRIVER");

    // Check that the client is persisted
    assertThat(clientRepository.findByMail("bob@mail.com")).isPresent();
  }
}
