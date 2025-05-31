package TQS.project.backend;

import TQS.project.backend.Config.TestSecurityConfig;
import TQS.project.backend.controller.BookingController;
import TQS.project.backend.dto.CreateBookingDTO;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestSecurityConfig.class)
@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockBean 
    private BookingService bookingService;

    @SuppressWarnings("removal")
    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Requirement("SCRUM-20")
    public void whenValidInput_thenReturnsOk() throws Exception {
        CreateBookingDTO dto = new CreateBookingDTO();
        dto.setMail("user@example.com");
        dto.setChargerId(1L);
        dto.setStartTime(LocalDateTime.now().plusDays(1));
        dto.setDuration(20);

        mockMvc.perform(post("/api/booking")
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

        System.out.println("DTO1: "+dto);
        System.out.println("DTO2: "+objectMapper.writeValueAsString(dto));
        mockMvc.perform(post("/api/booking")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}

