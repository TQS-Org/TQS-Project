package TQS.project.backend;

import TQS.project.backend.Config.TestSecurityConfig;
import TQS.project.backend.controller.BookingController;
import TQS.project.backend.dto.CreateBookingDTO;
import TQS.project.backend.entity.Booking;
import TQS.project.backend.security.JwtAuthFilter;
import TQS.project.backend.service.BookingService;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestSecurityConfig.class)
@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BookingControllerTest {

  @Autowired private MockMvc mockMvc;

  @SuppressWarnings("removal")
  @MockBean
  private BookingService bookingService;

  @SuppressWarnings("removal")
  @MockBean
  private JwtAuthFilter jwtAuthFilter;

  @Autowired private ObjectMapper objectMapper;

  private final Long chargerId = 1L;

  @Test
  @Requirement("SCRUM-20")
  public void whenValidInput_thenReturnsOk() throws Exception {
    CreateBookingDTO dto = new CreateBookingDTO();
    dto.setMail("user@example.com");
    dto.setChargerId(1L);
    dto.setStartTime(LocalDateTime.now().plusDays(1));
    dto.setDuration(20);

    mockMvc
        .perform(
            post("/api/booking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk());
  }

  @Test
  @Requirement("SCRUM-20")
  public void whenInvalidInput_thenReturnsBadRequest() throws Exception {
    CreateBookingDTO dto = new CreateBookingDTO();
    dto.setMail(""); // invalid
    dto.setChargerId(null); // invalid
    dto.setStartTime(null); // invalid
    dto.setDuration(1); // invalid

    mockMvc
        .perform(
            post("/api/booking")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @Requirement("SCRUM-20")
  public void testGetAllBookingsByCharger_withoutDate_returnsAllBookings() throws Exception {
    List<Booking> mockBookings = List.of(new Booking(), new Booking());

    when(bookingService.getAllBookingsByCharger(chargerId)).thenReturn(mockBookings);

    mockMvc
        .perform(get("/api/booking/charger/{id}", chargerId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));

    verify(bookingService, times(1)).getAllBookingsByCharger(chargerId);
    verify(bookingService, never()).getAllBookingsByDateAndCharger(anyLong(), any());
  }

  @Test
  @Requirement("SCRUM-20")
  public void testGetBookingsByCharger_withDate_returnsFilteredBookings() throws Exception {
    LocalDate date = LocalDate.of(2025, 6, 1);
    List<Booking> mockBookings = List.of(new Booking());

    when(bookingService.getAllBookingsByDateAndCharger(chargerId, date)).thenReturn(mockBookings);

    mockMvc
        .perform(get("/api/booking/charger/{id}", chargerId).param("date", date.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1));

    verify(bookingService, times(1)).getAllBookingsByDateAndCharger(chargerId, date);
    verify(bookingService, never()).getAllBookingsByCharger(anyLong());
  }

  @Test
  @Requirement("SCRUM-20")
  public void testGetBookingsByCharger_withNoResults_returnsEmptyList() throws Exception {
    when(bookingService.getAllBookingsByCharger(chargerId)).thenReturn(Collections.emptyList());

    mockMvc
        .perform(get("/api/booking/charger/{id}", chargerId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @Requirement("SCRUM-20")
  public void testGetBookingsByStation_invalidDateFormat_returnsBadRequest() throws Exception {
    mockMvc
        .perform(get("/api/booking/charger/{id}", chargerId).param("date", "not-a-date"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @Requirement("SCRUM-24")
  public void testGetAllBookingsByClient_returnsBookings() throws Exception {
    long clientId = 42L;
    List<Booking> mockBookings = List.of(new Booking(), new Booking());

    when(bookingService.getAllBookingsByClient(clientId)).thenReturn(mockBookings);

    mockMvc
        .perform(get("/api/booking/client/{id}", clientId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));

    verify(bookingService, times(1)).getAllBookingsByClient(clientId);
  }
}
