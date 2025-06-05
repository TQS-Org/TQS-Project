package TQS.project.backend;

import TQS.project.backend.controller.StationController;
import TQS.project.backend.dto.StationDTO;
import TQS.project.backend.entity.Charger;
import TQS.project.backend.entity.Station;
import TQS.project.backend.security.JwtAuthFilter;
import TQS.project.backend.security.JwtProvider;
import TQS.project.backend.service.StationService;
import TQS.project.backend.Config.TestSecurityConfig;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestSecurityConfig.class)
@WebMvcTest(StationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class StationControllerTest {

  @Autowired private MockMvc mockMvc;

  @SuppressWarnings("removal")
  @MockBean
  private StationService stationService;

  @SuppressWarnings("removal")
  @MockBean
  private JwtProvider jwtProvider;

  @SuppressWarnings("removal")
  @MockBean
  private JwtAuthFilter jwtAuthFilter;

  @Test
  @Requirement("SCRUM-16")
  void testGetAllStations() throws Exception {
    Station station1 = new Station();
    station1.setId(1L);
    station1.setName("Station One");

    Station station2 = new Station();
    station2.setId(2L);
    station2.setName("Station Two");

    when(stationService.getAllStations()).thenReturn(List.of(station1, station2));

    mockMvc
        .perform(get("/api/stations"))
        .andDo(
            result -> System.out.println("RESPONSE: " + result.getResponse().getContentAsString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].name").value("Station One"))
        .andExpect(jsonPath("$[1].name").value("Station Two"));
  }

  @Test
  @Requirement("SCRUM-16")
  void testGetStationById_found() throws Exception {
    Station station = new Station();
    station.setId(1L);
    station.setName("Test Station");

    when(stationService.getStationById(1L)).thenReturn(Optional.of(station));

    mockMvc
        .perform(get("/api/stations/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Test Station"));
  }

  @Test
  @Requirement("SCRUM-16")
  void testGetStationById_notFound() throws Exception {
    when(stationService.getStationById(999L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/stations/999")).andExpect(status().isNotFound());
  }

  @Test
  @Requirement("SCRUM-16")
  void testSearchStations() throws Exception {
    Station station = new Station();
    station.setId(1L);
    station.setName("Cheap Fast Charger");

    when(stationService.searchStations(
            anyString(),
            anyDouble(),
            anyString(),
            anyDouble(),
            anyDouble(),
            anyString(),
            anyBoolean()))
        .thenReturn(List.of(station));

    mockMvc
        .perform(
            get("/api/stations/search")
                .param("district", "Lisboa")
                .param("maxPrice", "0.40")
                .param("chargerType", "FAST")
                .param("minPower", "50.0")
                .param("maxPower", "150.0")
                .param("connectorType", "CCS")
                .param("available", "true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name").value("Cheap Fast Charger"));
  }

  @Test
  @Requirement("SCRUM-20")
  void testGetStationChargers_returnsChargers() throws Exception {
    Charger charger1 = new Charger();
    charger1.setId(1L);
    charger1.setType("AC");
    charger1.setConnectorType("Type2");
    charger1.setPower(22.0);
    charger1.setAvailable(true);

    Charger charger2 = new Charger();
    charger2.setId(2L);
    charger2.setType("DC");
    charger2.setConnectorType("CCS");
    charger2.setPower(50.0);
    charger2.setAvailable(true);

    List<Charger> chargers = List.of(charger1, charger2);

    when(stationService.getAllStationChargers(1L)).thenReturn(chargers);

    mockMvc
        .perform(get("/api/stations/1/chargers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].type").value("AC"))
        .andExpect(jsonPath("$[1].type").value("DC"));
  }

  @DisplayName("Tests for StationController createStation endpoint")
  @Test
  @Requirement("SCRUM-36")
  void testCreateStation_success() throws Exception {
    StationDTO stationDTO = new StationDTO();
    stationDTO.setName("New Station");
    stationDTO.setAddress("Lisboa");
    stationDTO.setLatitude(38.72);
    stationDTO.setLongitude(-9.13);
    stationDTO.setBrand("Chin Chan");

    Station createdStation = new Station();
    createdStation.setId(1L);
    createdStation.setName("New Station");

    when(stationService.createStation(any(StationDTO.class))).thenReturn(createdStation);

    mockMvc
        .perform(
            post("/api/stations")
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(stationDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("New Station"));
  }

  @Test
  @Requirement("SCRUM-36")
  void testCreateStation_validationFailure() throws Exception {
    StationDTO stationDTO = new StationDTO();
    // Missing required fields, e.g., name, district, etc.

    mockMvc
        .perform(
            post("/api/stations")
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(stationDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @Requirement("SCRUM-36")
  void testUpdateStation_success() throws Exception {
    StationDTO dto = new StationDTO();
    dto.setName("Updated Station");
    dto.setAddress("Lisboa");
    dto.setLatitude(38.72);
    dto.setLongitude(-9.13);
    dto.setBrand("CHIN CHAN");
    dto.setPrice(10.0);
    dto.setClosingHours("23:55");
    dto.setOpeningHours("01:00");

    Station updatedStation = new Station();
    updatedStation.setId(1L);
    updatedStation.setName("Updated Station");

    when(stationService.updateStation(eq(1L), any(StationDTO.class))).thenReturn(updatedStation);

    mockMvc
        .perform(
            put("/api/stations/1")
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.name").value("Updated Station"));
  }

  @Test
  @Requirement("SCRUM-36")
  void testUpdateStation_validationFails() throws Exception {
    StationDTO dto = new StationDTO();
    // Missing required fields

    mockMvc
        .perform(
            put("/api/stations/1")
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(dto)))
        .andExpect(status().isBadRequest());
  }
}
