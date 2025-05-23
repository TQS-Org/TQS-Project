package TQS.project.backend;

import TQS.project.backend.entity.Station;
import TQS.project.backend.service.StationService;
import TQS.project.backend.entity.Charger;
import TQS.project.backend.repository.StationRepository;
import TQS.project.backend.repository.ChargerRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class StationServiceTest {

    @Mock
    private StationRepository stationRepository;

    @Mock
    private ChargerRepository chargerRepository;

    @InjectMocks
    private StationService stationService;

    @Test
    void testGetAllStations() {
        Station s1 = new Station();
        s1.setId(1L);
        s1.setName("Alpha");

        Station s2 = new Station();
        s2.setId(2L);
        s2.setName("Beta");

        when(stationRepository.findAll()).thenReturn(List.of(s1, s2));

        List<Station> result = stationService.getAllStations();

        assertEquals(2, result.size());
        assertEquals("Alpha", result.get(0).getName());
    }

    @Test
    void testGetStationById_found() {
        Station station = new Station();
        station.setId(1L);
        station.setName("Gamma");

        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));

        Optional<Station> result = stationService.getStationById(1L);

        assertTrue(result.isPresent());
        assertEquals("Gamma", result.get().getName());
    }

    @Test
    void testGetStationById_notFound() {
        when(stationRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Station> result = stationService.getStationById(2L);

        assertFalse(result.isPresent());
    }

    @Test
    void testSearchStations_withMatchingChargerAndStationFilters() {
        Station station = new Station();
        station.setId(1L);
        station.setName("Filtered");
        station.setAddress("Lisboa");
        station.setPrice(0.35);

        Charger charger = new Charger();
        charger.setId(1L);
        charger.setType("FAST");
        charger.setConnectorType("CCS");
        charger.setPower(100.0);
        charger.setAvailable(true);
        charger.setStation(station);

        when(chargerRepository.findAll()).thenReturn(List.of(charger));
        when(stationRepository.findAll()).thenReturn(List.of(station));

        List<Station> result = stationService.searchStations("Lisboa", 0.40, "FAST", 50.0, 150.0, "CCS", true);

        assertEquals(1, result.size());
        assertEquals("Filtered", result.get(0).getName());
    }

    @Test
    void testSearchStations_noChargerMatch() {
        Station station = new Station();
        station.setId(1L);
        station.setName("Filtered");
        station.setAddress("Lisboa");
        station.setPrice(0.35);

        Charger charger = new Charger();
        charger.setId(1L);
        charger.setType("SLOW"); // won't match
        charger.setConnectorType("Type2");
        charger.setPower(22.0);
        charger.setAvailable(true);
        charger.setStation(station);

        when(chargerRepository.findAll()).thenReturn(List.of(charger));
        when(stationRepository.findAll()).thenReturn(List.of(station));

        List<Station> result = stationService.searchStations("Lisboa", 0.40, "FAST", 50.0, 150.0, "CCS", true);

        assertEquals(0, result.size());
    }
}
