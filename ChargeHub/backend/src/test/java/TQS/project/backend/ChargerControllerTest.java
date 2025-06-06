package TQS.project.backend;

import TQS.project.backend.Config.TestSecurityConfig;
import TQS.project.backend.controller.ChargerController;
import TQS.project.backend.dto.ChargerDTO;
import TQS.project.backend.entity.Charger;
import TQS.project.backend.security.JwtAuthFilter;
import TQS.project.backend.security.JwtProvider;
import TQS.project.backend.service.ChargerService;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

  @DisplayName("Tests for ChargerController POST endpoint")
  @Test
  @Requirement("SCRUM-36")
  void createChargerForStation_success() throws Exception {
    ChargerDTO chargerDTO = new ChargerDTO();
    chargerDTO.setType("DC");
    chargerDTO.setConnectorType("CCS");
    chargerDTO.setPower(50.0);
    chargerDTO.setAvailable(true);

    Charger createdCharger = new Charger();
    createdCharger.setId(1L);
    createdCharger.setType("DC");
    createdCharger.setConnectorType("CCS");
    createdCharger.setPower(50.0);
    createdCharger.setAvailable(true);

    when(chargerService.createChargerForStation(100L, chargerDTO)).thenReturn(createdCharger);

    System.out.println("Mocked createdCharger: " + createdCharger);

    mockMvc
        .perform(
            post("/api/charger/100")
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(chargerDTO)))
        .andExpect(status().isOk());
  }

  @Test
  @Requirement("SCRUM-36")
  void createChargerForStation_validationFails() throws Exception {
    ChargerDTO chargerDTO = new ChargerDTO();
    // Invalid data: missing required fields, e.g., type, connectorType, etc.

    mockMvc
        .perform(
            post("/api/charger/100")
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(chargerDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @Requirement("SCRUM-36")
  void testUpdateCharger_success() throws Exception {
    ChargerDTO dto = new ChargerDTO();
    dto.setType("DC");
    dto.setConnectorType("CCS");
    dto.setPower(60.0);
    dto.setAvailable(true);

    Charger updatedCharger = new Charger();
    updatedCharger.setId(1L);
    updatedCharger.setType("DC");
    updatedCharger.setPower(60.0);

    when(chargerService.updateCharger(eq(1L), any(ChargerDTO.class))).thenReturn(updatedCharger);

    mockMvc
        .perform(
            put("/api/charger/1")
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.type").value("DC"))
        .andExpect(jsonPath("$.power").value(60.0));
  }

  @Test
  @Requirement("SCRUM-36")
  void testUpdateCharger_validationFails() throws Exception {
    ChargerDTO dto = new ChargerDTO();
    // Missing required fields (e.g., type, connectorType, etc.)

    mockMvc
        .perform(
            put("/api/charger/1")
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(dto)))
        .andExpect(status().isBadRequest());
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
        .perform(
            put("/api/charger/1/session/10")
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
        .perform(
            put("/api/charger/1/session/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
        .andExpect(status().isNotFound())
        .andExpect(content().string("Charging session not found"));
  }
}
