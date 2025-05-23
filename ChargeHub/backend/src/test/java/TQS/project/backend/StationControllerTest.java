package TQS.project.backend;

import TQS.project.backend.controller.StationController;
import TQS.project.backend.entity.Station;
import TQS.project.backend.security.JwtAuthFilter;
import TQS.project.backend.security.JwtProvider;
import TQS.project.backend.service.StationService;
import TQS.project.backend.Config.TestSecurityConfig;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestSecurityConfig.class)
@WebMvcTest(StationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class StationControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
    void testGetAllStations() throws Exception {
        Station station1 = new Station();
        station1.setId(1L);
        station1.setName("Station One");

        Station station2 = new Station();
        station2.setId(2L);
        station2.setName("Station Two");

        when(stationService.getAllStations()).thenReturn(List.of(station1, station2));

        mockMvc.perform(get("/api/stations"))
                .andDo(result -> System.out.println("RESPONSE: " + result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Station One"))
                .andExpect(jsonPath("$[1].name").value("Station Two"));
    }

    @Test
    void testGetStationById_found() throws Exception {
        Station station = new Station();
        station.setId(1L);
        station.setName("Test Station");

        when(stationService.getStationById(1L)).thenReturn(Optional.of(station));

        mockMvc.perform(get("/api/stations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Station"));
    }

    @Test
    void testGetStationById_notFound() throws Exception {
        when(stationService.getStationById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/stations/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSearchStations() throws Exception {
        Station station = new Station();
        station.setId(1L);
        station.setName("Cheap Fast Charger");

        when(stationService.searchStations(anyString(), anyDouble(), anyString(), anyDouble(), anyDouble(), anyString(),
                anyBoolean()))
                .thenReturn(List.of(station));

        mockMvc.perform(get("/api/stations/search")
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
}
