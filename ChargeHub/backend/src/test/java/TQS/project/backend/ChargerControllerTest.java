package TQS.project.backend;

import TQS.project.backend.Config.TestSecurityConfig;
import TQS.project.backend.controller.ChargerController;
import TQS.project.backend.entity.Charger;
import TQS.project.backend.security.JwtAuthFilter;
import TQS.project.backend.security.JwtProvider;
import TQS.project.backend.service.ChargerService;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.when;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

import TQS.project.backend.dto.ChargerTokenDTO;
import TQS.project.backend.dto.FinishedChargingSessionDTO;

import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@Import(TestSecurityConfig.class)
@WebMvcTest(ChargerController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ChargerControllerTest {

  @Autowired private MockMvc mockMvc;

  @SuppressWarnings("removal")
  @MockBean
  private ChargerService chargerService;

  @SuppressWarnings("removal")
  @MockBean
  private JwtProvider jwtProvider;

  @SuppressWarnings("removal")
  @MockBean
  private JwtAuthFilter jwtAuthFilter;

  @Test
  @Requirement("SCRUM-20")
  void getChargerById_existingId_returnsCharger() throws Exception {
    Charger charger = new Charger();
    charger.setId(1L);
    charger.setType("AC");
    charger.setPower(22.0);

    when(chargerService.getChargerById(1L)).thenReturn(Optional.of(charger));

    mockMvc
        .perform(get("/api/charger/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.type").value("AC"))
        .andExpect(jsonPath("$.power").value(22.0));
  }

  @Test
  @Requirement("SCRUM-20")
  void getChargerById_nonExistingId_returns404() throws Exception {
    when(chargerService.getChargerById(99L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/charger/99")).andExpect(status().isNotFound());
  }

  @Test
  @Requirement("SCRUM-24")
  void createChargingSession_validRequest_returnsOk() throws Exception {
    ChargerTokenDTO dto = new ChargerTokenDTO();
    dto.setChargeToken("VALIDTOKEN");

    mockMvc
        .perform(
            post("/api/charger/1/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "chargeToken": "VALIDTOKEN"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(content().string("Charger unlocked successfully, charge session starting..."));

    verify(chargerService).startChargingSession("VALIDTOKEN", 1L);
  }

  @Test
  @Requirement("SCRUM-24")
  void createChargingSession_invalidToken_returnsBadRequest() throws Exception {
    doThrow(new IllegalArgumentException("No booking found for the given token."))
        .when(chargerService)
        .startChargingSession("BADTOKEN", 1L);

    mockMvc
        .perform(
            post("/api/charger/1/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "chargeToken": "BADTOKEN"
                    }
                    """))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("No booking found for the given token."));
  }

  @Test
  @Requirement("SCRUM-27")
  void finishChargingSession_validRequest_returnsOk() throws Exception {
    FinishedChargingSessionDTO dto = new FinishedChargingSessionDTO(20.0f, LocalDateTime.now());
  
    ObjectMapper mapper = new ObjectMapper();
    mapper.findAndRegisterModules(); // handle JavaTime (LocalDateTime)
  
    mockMvc
        .perform(put("/api/charger/1/session/10")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(content().string("Charging session successfully concluded."));
  
    verify(chargerService).finishChargingSession(eq(10L), any(FinishedChargingSessionDTO.class));
  }
  
  @Test
  @Requirement("SCRUM-27")
  void finishChargingSession_invalidSession_returnsNotFound() throws Exception {
    FinishedChargingSessionDTO dto = new FinishedChargingSessionDTO(10.0f, LocalDateTime.now());
  
    doThrow(new IllegalArgumentException("Charging session not found"))
        .when(chargerService)
        .finishChargingSession(eq(999L), any(FinishedChargingSessionDTO.class));
  
    ObjectMapper mapper = new ObjectMapper();
    mapper.findAndRegisterModules();
  
    mockMvc
        .perform(put("/api/charger/1/session/999")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(dto)))
        .andExpect(status().isNotFound())
        .andExpect(content().string("Charging session not found"));
  }

}
