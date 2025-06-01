package TQS.project.backend;

import TQS.project.backend.dto.CreateBookingDTO;
import TQS.project.backend.dto.LoginRequest;
import TQS.project.backend.dto.LoginResponse;
import TQS.project.backend.entity.Charger;
import TQS.project.backend.entity.Client;
import TQS.project.backend.entity.Station;
import TQS.project.backend.repository.BookingRepository;
import TQS.project.backend.repository.ChargerRepository;
import TQS.project.backend.repository.ClientRepository;
import TQS.project.backend.repository.StationRepository;
import TQS.project.backend.repository.StaffRepository;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookingIT {

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private ClientRepository clientRepository;
    @Autowired private ChargerRepository chargerRepository;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private String token;
    private static Long testChargerId;

    @BeforeEach
    void setup() {
        bookingRepository.deleteAll();

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
        
        testChargerId = chargerRepository.findAll().stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No charger available for testing"))
            .getId();
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return headers;
    }

    @Test
    @Order(1)
    @Requirement("SCRUM-20")
    void createValidBooking_thenReturnSuccessMessage() {
        CreateBookingDTO bookingDTO = new CreateBookingDTO();
        bookingDTO.setMail("driver@mail.com");
        bookingDTO.setChargerId(testChargerId);
        bookingDTO.setStartTime(LocalDateTime.now().plusHours(10));
        bookingDTO.setDuration(30);

        HttpHeaders headers = createAuthHeaders();

        HttpEntity<CreateBookingDTO> request = new HttpEntity<>(bookingDTO, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/api/booking",
            request,
            String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(bookingRepository.count()).isEqualTo(1);
    }

    @Test
    @Order(2)
    @Requirement("SCRUM-20")
    void createInvalidBooking_thenReturnError400() {
        CreateBookingDTO bookingDTO = new CreateBookingDTO();
        bookingDTO.setMail("");
        bookingDTO.setChargerId(null);
        bookingDTO.setStartTime(null);
        bookingDTO.setDuration(0);

        ResponseEntity<String> response = restTemplate.exchange(
            "/api/booking",
            HttpMethod.POST,
            new HttpEntity<>(bookingDTO, createAuthHeaders()),
            String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    @Order(3)
    @Requirement("SCRUM-20")
    void createBookingOnInvalidSchedule_thenReturnError409() {
        // First booking
        CreateBookingDTO firstBooking = new CreateBookingDTO();
        firstBooking.setMail("driver@mail.com");
        firstBooking.setChargerId(testChargerId);
        firstBooking.setStartTime(LocalDateTime.now().plusHours(10));
        firstBooking.setDuration(60);
        
        ResponseEntity<String> firstResponse = restTemplate.exchange(
            "/api/booking",
            HttpMethod.POST,
            new HttpEntity<>(firstBooking, createAuthHeaders()),
            String.class);
        
        assertThat(firstResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Overlapping booking
        CreateBookingDTO overlappingBooking = new CreateBookingDTO();
        overlappingBooking.setMail("driver@mail.com");
        overlappingBooking.setChargerId(testChargerId);
        overlappingBooking.setStartTime(LocalDateTime.now().plusHours(10).plusMinutes(30));
        overlappingBooking.setDuration(60);

        ResponseEntity<String> response = restTemplate.exchange(
            "/api/booking",
            HttpMethod.POST,
            new HttpEntity<>(overlappingBooking, createAuthHeaders()),
            String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}