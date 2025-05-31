package TQS.project.backend;

import TQS.project.backend.Config.TestSecurityConfig;
import TQS.project.backend.controller.BookingController;
import TQS.project.backend.dto.CreateBookingDTO;
import TQS.project.backend.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestSecurityConfig.class)
@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void whenValidInput_thenReturnsOk() throws Exception {
        CreateBookingDTO dto = new CreateBookingDTO();
        dto.setMail("user@example.com");
        dto.setStationId(1L);
        dto.setChargerId(1L);
        dto.setStartTime(LocalDateTime.now().plusDays(1));
        dto.setDuration(20);

        // Simulate successful creation response (you can return anything appropriate)
        Mockito.when(bookingService.createBooking(Mockito.any())).thenReturn("mock-token");

        mockMvc.perform(post("/api/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("mock-token"));
    }

    @Test
    public void whenInvalidInput_thenReturnsBadRequest() throws Exception {
        CreateBookingDTO dto = new CreateBookingDTO(); // empty DTO = invalid

        mockMvc.perform(post("/api/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}

