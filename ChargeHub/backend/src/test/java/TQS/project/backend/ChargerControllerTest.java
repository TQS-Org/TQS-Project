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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import TQS.project.backend.dto.ChargerTokenDTO;
import org.springframework.http.MediaType;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.http.MediaType;

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
}
