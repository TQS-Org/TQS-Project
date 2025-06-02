package TQS.project.backend;

import org.junit.jupiter.api.Test;

import TQS.project.backend.entity.Charger;
import TQS.project.backend.repository.ChargerRepository;
import TQS.project.backend.service.ChargerService;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Optional;

public class ChargerServiceTest {

    private final ChargerRepository chargerRepository = mock(ChargerRepository.class);
    private final ChargerService chargerService = new ChargerService(chargerRepository);

    @Test
    @Requirement("SCRUM-20")
    void getChargerById_existingId_returnsCharger() {
        Charger charger = new Charger();
        charger.setId(1L);
        charger.setType("DC");

        when(chargerRepository.findById(1L)).thenReturn(Optional.of(charger));

        Optional<Charger> result = chargerService.getChargerById(1L);

        assertThat(result).isPresent();
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
