package TQS.project.backend;

import TQS.project.backend.dto.CreateBookingDTO;
import TQS.project.backend.dto.LoginRequest;
import TQS.project.backend.dto.LoginResponse;
import TQS.project.backend.entity.Booking;
import TQS.project.backend.entity.Charger;
import TQS.project.backend.entity.Client;
import TQS.project.backend.entity.Station;
import TQS.project.backend.repository.BookingRepository;
import TQS.project.backend.repository.ChargerRepository;
import TQS.project.backend.repository.ChargingSessionRepository;
import TQS.project.backend.repository.StationRepository;
import TQS.project.backend.repository.StaffRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
public class BookingIT {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private ClientRepository clientRepository;
  @Autowired private ChargerRepository chargerRepository;
  @Autowired private ChargingSessionRepository chargingSessionRepository;
  @Autowired private BookingRepository bookingRepository;
  @Autowired private StationRepository stationRepository;
  @Autowired private StaffRepository staffRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  private String token;
  private Long testChargerId;

  @BeforeEach
  void setup() {
    // Clean up repositories
    chargingSessionRepository.deleteAllInBatch();
    bookingRepository.deleteAllInBatch();
    chargerRepository.deleteAllInBatch();
    staffRepository.deleteAllInBatch();
    stationRepository.deleteAllInBatch();
    clientRepository.deleteAllInBatch();

    // Create and save a client
    Client client = new Client();
    client.setMail("driver@mail.com");
    client.setPassword(passwordEncoder.encode("driverpass"));
    client.setName("Driver One");
    client.setAge(30);
    client.setNumber("123456789");
    clientRepository.save(client);

    // Authenticate and retrieve JWT token
    LoginRequest login = new LoginRequest("driver@mail.com", "driverpass");
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<LoginRequest> request = new HttpEntity<>(login, headers);

    ResponseEntity<LoginResponse> response =
        restTemplate.postForEntity("/api/auth/login", request, LoginResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    token = response.getBody().getToken();

    // Create and save a test station
    Station station = new Station();
    station.setName("Integration Test Station");
    station.setAddress("Test Ave 123");
    station.setOpeningHours("08:00");
    station.setClosingHours("22:00");
    station = stationRepository.save(station);

    // Create and save a test charger
    Charger charger = new Charger();
    charger.setType("DC");
    charger.setConnectorType("CCS");
    charger.setPower(100);
    charger.setAvailable(true);
    charger.setStation(station);
    charger = chargerRepository.save(charger);

    testChargerId = charger.getId();
  }

  @Test
  @Requirement("SCRUM-20")
  void createValidBooking_thenReturnSuccessMessage() {
    CreateBookingDTO bookingDTO = new CreateBookingDTO();
    bookingDTO.setMail("driver@mail.com");
    bookingDTO.setChargerId(testChargerId);
    bookingDTO.setStartTime(LocalDateTime.of(2025, Month.JUNE, 25, 14, 30));
    bookingDTO.setDuration(30);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<CreateBookingDTO> request = new HttpEntity<>(bookingDTO, headers);

    ResponseEntity<String> response =
        restTemplate.postForEntity("/api/booking", request, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(bookingRepository.count()).isEqualTo(1);
  }

  @Test
  @Requirement("SCRUM-20")
  void createInvalidBooking_thenReturnError400() {
    CreateBookingDTO bookingDTO = new CreateBookingDTO();
    bookingDTO.setMail("");
    bookingDTO.setChargerId(null);
    bookingDTO.setStartTime(null);
    bookingDTO.setDuration(0);

    ResponseEntity<String> response =
        restTemplate.exchange(
            "/api/booking",
            HttpMethod.POST,
            new HttpEntity<>(bookingDTO, createAuthHeaders()),
            String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotEmpty();
  }

  @Test
  @Requirement("SCRUM-20")
  void createBookingOnInvalidSchedule_thenReturnError400() {
    // First booking
    CreateBookingDTO firstBooking = new CreateBookingDTO();
    firstBooking.setMail("driver@mail.com");
    firstBooking.setChargerId(testChargerId);
    firstBooking.setStartTime(LocalDateTime.of(2025, Month.JUNE, 25, 14, 30));
    firstBooking.setDuration(60);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<CreateBookingDTO> request = new HttpEntity<>(firstBooking, headers);

    ResponseEntity<String> firstResponse =
        restTemplate.postForEntity("/api/booking", request, String.class);

    assertThat(firstResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

    // Overlapping booking
    CreateBookingDTO overlappingBooking = new CreateBookingDTO();
    overlappingBooking.setMail("driver@mail.com");
    overlappingBooking.setChargerId(testChargerId);
    overlappingBooking.setStartTime(LocalDateTime.of(2025, Month.JUNE, 25, 15, 00));
    overlappingBooking.setDuration(60);

    HttpEntity<CreateBookingDTO> request2 = new HttpEntity<>(overlappingBooking, headers);

    ResponseEntity<String> response =
        restTemplate.postForEntity("/api/booking", request2, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  @Requirement("SCRUM-20")
  void getBookingsByCharger_withDate_returnsFilteredBookings() {

    // Create test data
    Station station = new Station();
    // station.setId(100L);
    station.setName("Test Station");
    station.setOpeningHours("08:00");
    station.setClosingHours("20:00");
    station = stationRepository.save(station);

    Charger charger = new Charger();
    charger.setStation(station);
    charger = chargerRepository.save(charger);

    // Create test bookings
    LocalDate today = LocalDate.now();
    LocalDateTime time1 = LocalDateTime.of(today, LocalTime.of(10, 0));
    LocalDateTime time2 = LocalDateTime.of(today, LocalTime.of(14, 0));
    LocalDateTime time3 = LocalDateTime.of(today.plusDays(1), LocalTime.of(11, 0));

    Client client = clientRepository.findByMail("driver@mail.com").get();

    Booking booking1 = new Booking(client, charger, time1, 60);
    Booking booking2 = new Booking(client, charger, time2, 30);
    Booking booking3 = new Booking(client, charger, time3, 45);

    bookingRepository.saveAll(List.of(booking1, booking2, booking3));

    // Test with date filter
    ResponseEntity<Booking[]> response =
        restTemplate.exchange(
            "/api/booking/charger/" + charger.getId() + "?date=" + today,
            HttpMethod.GET,
            new HttpEntity<>(createAuthHeaders()),
            Booking[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(2);
    assertThat(response.getBody())
        .extracting(Booking::getStartTime)
        .containsExactlyInAnyOrder(time1, time2);
  }

  @Test
  @Requirement("SCRUM-20")
  void getBookingsByCharger_withoutDate_returnsAllBookings() {
    // Create test data
    Station station = new Station();
    // station.setId(101L);
    station.setName("Test Station");
    station.setOpeningHours("08:00");
    station.setClosingHours("20:00");
    station = stationRepository.save(station);

    Charger charger = new Charger();
    charger.setStation(station);
    charger = chargerRepository.save(charger);

    // Create test bookings
    LocalDate today = LocalDate.now();
    LocalDateTime time1 = LocalDateTime.of(today, LocalTime.of(10, 0));
    LocalDateTime time2 = LocalDateTime.of(today.plusDays(1), LocalTime.of(14, 0));

    Client client = clientRepository.findByMail("driver@mail.com").get();

    Booking booking1 = new Booking(client, charger, time1, 60);
    Booking booking2 = new Booking(client, charger, time2, 30);

    bookingRepository.saveAll(List.of(booking1, booking2));

    // Test without date filter
    ResponseEntity<Booking[]> response =
        restTemplate.exchange(
            "/api/booking/charger/" + charger.getId(),
            HttpMethod.GET,
            new HttpEntity<>(createAuthHeaders()),
            Booking[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(2);
    assertThat(response.getBody())
        .extracting(Booking::getStartTime)
        .containsExactlyInAnyOrder(time1, time2);
  }

  @Test
  @Requirement("SCRUM-20")
  void getBookingsByCharger_noBookings_returnsEmptyList() {
    // Create test station
    Station station = new Station();
    // station.setId(102L);
    station.setName("Empty Station");
    station.setOpeningHours("08:00");
    station.setClosingHours("20:00");
    station = stationRepository.save(station);

    Charger charger = new Charger();
    charger.setStation(station);
    charger = chargerRepository.save(charger);

    // Test with no bookings
    ResponseEntity<Booking[]> response =
        restTemplate.exchange(
            "/api/booking/charger/" + charger.getId(),
            HttpMethod.GET,
            new HttpEntity<>(createAuthHeaders()),
            Booking[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEmpty();
  }

  @Test
  @Requirement("SCRUM-24")
  void whenGetBookingsByClientId_thenReturnCorrectBookings() {
    // Given: Create test client
    Client client = new Client();
    client.setMail("bookingtest@mail.com");
    client.setPassword(passwordEncoder.encode("bookingpass"));
    client.setName("Booking Tester");
    client.setAge(25);
    client.setNumber("111222333");
    client = clientRepository.save(client);

    // Create station and charger
    Station station =
        new Station(
            "Repo Test Station", "TestBrand", 0.0, 0.0, "Test Addr", 4, "08:00", "22:00", 0.25);
    station = stationRepository.save(station);

    Charger charger = new Charger("AC", 22.0, true, "Type2");
    charger.setStation(station);
    charger = chargerRepository.save(charger);

    // Create bookings
    LocalDateTime now = LocalDateTime.now();
    Booking booking1 = new Booking(client, charger, now.plusHours(1), 30);
    Booking booking2 = new Booking(client, charger, now.plusHours(2), 45);
    bookingRepository.saveAll(List.of(booking1, booking2));

    // Login the test client
    LoginRequest loginRequest = new LoginRequest(client.getMail(), "bookingpass");
    HttpHeaders loginHeaders = new HttpHeaders();
    loginHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<LoginRequest> loginEntity = new HttpEntity<>(loginRequest, loginHeaders);

    ResponseEntity<LoginResponse> loginResponse =
        restTemplate.postForEntity("/api/auth/login", loginEntity, LoginResponse.class);

    assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    String clientToken = loginResponse.getBody().getToken();

    // When: Perform GET request to fetch bookings
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(clientToken);
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<Booking[]> response =
        restTemplate.exchange(
            "/api/booking/client/" + client.getId(), HttpMethod.GET, entity, Booking[].class);

    // Then: Validate bookings were returned
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Booking[] bookings = response.getBody();
    assertThat(bookings).isNotNull();
    assertThat(bookings).hasSize(2);
    assertThat(Arrays.stream(bookings).map(Booking::getDuration)).containsExactlyInAnyOrder(30, 45);
  }

  private HttpHeaders createJsonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  private HttpHeaders createAuthHeaders() {
    HttpHeaders headers = createJsonHeaders();
    headers.set("Authorization", "Bearer " + token);
    return headers;
  }
} //
