package TQS.project.backend;

import TQS.project.backend.Config.TestSecurityConfig;
import TQS.project.backend.controller.ChargerController;
import TQS.project.backend.entity.Charger;
import TQS.project.backend.service.ChargerService;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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

@Import(TestSecurityConfig.class)
@WebMvcTest(ChargerController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ChargerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChargerService chargerService;

    @Test
    @Requirement("SCRUM-20")
    void getChargerById_existingId_returnsCharger() throws Exception {
        Charger charger = new Charger();
        charger.setId(1L);
        charger.setType("AC");
        charger.setPower(22.0);

        when(chargerService.getChargerById(1L)).thenReturn(Optional.of(charger));

        mockMvc.perform(get("/api/chargers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.type").value("AC"))
                .andExpect(jsonPath("$.power").value(22.0));
    }

    @Test
    @Requirement("SCRUM-20")
    void getChargerById_nonExistingId_returns404() throws Exception {
        when(chargerService.getChargerById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/chargers/99"))
                .andExpect(status().isNotFound());
    }
}
