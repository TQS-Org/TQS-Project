package TQS.project.backend;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import TQS.project.backend.entity.Charger;
import TQS.project.backend.entity.Station;
import TQS.project.backend.repository.ChargerRepository;
import TQS.project.backend.service.ChargerService;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ChargerServiceTest {

    @Mock 
    private ChargerRepository chargerRepository;

    @InjectMocks
    private ChargerService chargerService;

    @Test
    @Requirement("SCRUM-20")
    void getChargerById_existingId_returnsCharger() {
        Station station = new Station();

        Charger charger = new Charger();
        charger.setStation(station);
        charger.setId(1L);
        charger.setType("DC");
    
        when(chargerRepository.findById(1L)).thenReturn(Optional.of(charger));
    
        Optional<Charger> result = chargerService.getChargerById(1L);
    
        assertThat(result.isPresent());
        assertThat(result.get().getType()).isEqualTo("DC");
    }


    @Test
    @Requirement("SCRUM-20")
    void getChargerById_nonExistingId_returnsEmpty() {
        when(chargerRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Charger> result = chargerService.getChargerById(2L);

        assertThat(result).isEmpty();
    }
}
