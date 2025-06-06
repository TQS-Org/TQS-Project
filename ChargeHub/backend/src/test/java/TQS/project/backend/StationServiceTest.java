package TQS.project.backend;

import TQS.project.backend.entity.Station;
import TQS.project.backend.service.StationService;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import TQS.project.backend.dto.StationDTO;
import TQS.project.backend.entity.Charger;
import TQS.project.backend.repository.StationRepository;
import TQS.project.backend.repository.ChargerRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class StationServiceTest {

  @Mock private StationRepository stationRepository;

  @Mock private ChargerRepository chargerRepository;

  @InjectMocks private StationService stationService;

  @Test
  @Requirement("SCRUM-16")
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
  @Requirement("SCRUM-16")
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
  @Requirement("SCRUM-16")
  void testGetStationById_notFound() {
    when(stationRepository.findById(2L)).thenReturn(Optional.empty());

    Optional<Station> result = stationService.getStationById(2L);

    assertFalse(result.isPresent());
  }

  @Test
  @Requirement("SCRUM-16")
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

    List<Station> result =
        stationService.searchStations("Lisboa", 0.40, "FAST", 50.0, 150.0, "CCS", true);

    assertEquals(1, result.size());
    assertEquals("Filtered", result.get(0).getName());
  }

  @Test
  @Requirement("SCRUM-16")
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

    List<Station> result =
        stationService.searchStations("Lisboa", 0.40, "FAST", 50.0, 150.0, "CCS", true);

    assertEquals(0, result.size());
  }

  @Test
  @Requirement("SCRUM-20")
  void testGetAllStationChargers_returnsChargerList() {
    Station station = new Station();
    station.setId(1L);
    station.setName("Charger Station");

    Charger charger1 = new Charger();
    charger1.setId(1L);
    charger1.setType("AC");
    charger1.setConnectorType("Type2");
    charger1.setPower(22.0);
    charger1.setAvailable(true);
    charger1.setStation(station);

    Charger charger2 = new Charger();
    charger2.setId(2L);
    charger2.setType("DC");
    charger2.setConnectorType("CCS");
    charger2.setPower(100.0);
    charger2.setAvailable(true);
    charger2.setStation(station);

    when(chargerRepository.findAllByStationId(1L)).thenReturn(List.of(charger1, charger2));

    List<Charger> result = stationService.getAllStationChargers(1L);

    assertEquals(2, result.size());
    assertEquals("AC", result.get(0).getType());
    assertEquals("DC", result.get(1).getType());
  }

  @Test
  @Requirement("SCRUM-36")
  void testCreateStation_success() {
    StationDTO dto = new StationDTO();
    dto.setName("New Station");
    dto.setBrand("Tesla");
    dto.setLatitude(38.72);
    dto.setLongitude(-9.13);
    dto.setAddress("Lisboa");
    dto.setNumberOfChargers(10);
    dto.setOpeningHours("08:00");
    dto.setClosingHours("22:00");
    dto.setPrice(0.30);

    Station savedStation = new Station();
    savedStation.setId(1L);
    savedStation.setName("New Station");

    when(stationRepository.save(any(Station.class))).thenReturn(savedStation);

    Station result = stationService.createStation(dto);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("New Station", result.getName());
    verify(stationRepository, times(1)).save(any(Station.class));
  }

  @Test
  @Requirement("SCRUM-36")
  void testUpdateStation_success() {
    Station existing = new Station();
    existing.setId(1L);
    existing.setName("Old Name");

    StationDTO dto = new StationDTO();
    dto.setName("Updated Name");
    dto.setAddress("New Address");
    dto.setLatitude(38.72);
    dto.setLongitude(-9.13);
    dto.setPrice(0.40);
    dto.setOpeningHours("08:00");
    dto.setClosingHours("22:00");

    when(stationRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(stationRepository.save(any(Station.class))).thenReturn(existing);

    Station result = stationService.updateStation(1L, dto);

    assertNotNull(result);
    assertEquals("Updated Name", result.getName());
    assertEquals("New Address", result.getAddress());
    assertEquals(0.40, result.getPrice());
    verify(stationRepository, times(1)).save(any(Station.class));
  }

  @Test
  @Requirement("SCRUM-36")
  void testUpdateStation_stationNotFound_throwsException() {
    StationDTO dto = new StationDTO();
    dto.setName("Updated Name");

    when(stationRepository.findById(1L)).thenReturn(Optional.empty());

    RuntimeException thrown =
        assertThrows(RuntimeException.class, () -> stationService.updateStation(1L, dto));

    assertEquals("Station not found", thrown.getMessage());
    verify(stationRepository, never()).save(any());
  }
}
