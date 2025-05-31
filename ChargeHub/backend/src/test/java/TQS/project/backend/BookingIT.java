package TQS.project.backend;

import TQS.project.backend.dto.LoginRequest;
import TQS.project.backend.dto.LoginResponse;
import TQS.project.backend.entity.Client;
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
public class BookingIT {

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private ClientRepository clientRepository; 

  @Autowired private PasswordEncoder passwordEncoder;

  public BookingIT(TestRestTemplate restTemplate, ClientRepository clientRepository, PasswordEncoder passwordEncoder){
    this.passwordEncoder = passwordEncoder;
    this.clientRepository = clientRepository;
    this.restTemplate = restTemplate;
  }

  private String token;

  @BeforeEach
  void setup() {
    // Optional cleanup
    clientRepository.deleteAll();

    // Insert admin manually
    Client driver = new Client();
    driver.setMail("user1@mail.com");
    driver.setPassword(passwordEncoder.encode("userpass"));
    driver.setName("User 1");
    driver.setAge(40);
    driver.setNumber("988888888");
    clientRepository.save(driver);

    // Login as admin
    LoginRequest login = new LoginRequest("user1@mail.com", "userpass");
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
  void createValidBooking_thenReturnSuccessMessage() {
    
  }

  @Test
  @Requirement("SCRUM-20")
  void createInvalidBooking_thenReturnError400() {
    
  }

  @Test
  @Requirement("SCRUM-20")
  void createBookingOnInvalidSchedule_thenReturnError409() {
    
  }
}
