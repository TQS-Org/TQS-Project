package TQS.project.backend.Login;

import TQS.project.backend.entity.Client;
import TQS.project.backend.repository.ClientRepository;
import TQS.project.backend.TestcontainersConfiguration;
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

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        Client client = new Client();
        client.setName("Alice");
        client.setMail("alice@example.com");
        client.setPassword(passwordEncoder.encode("plainpass")); // make sure this matches the encoder
        clientRepository.save(client);
    }

    @Test
    void testLoginSuccess() {
        var loginPayload = new LoginRequest("alice@example.com", "plainpass"); // password depends on your encoder

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LoginRequest> request = new HttpEntity<>(loginPayload, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/login", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("token");
    }
}
