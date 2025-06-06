package TQS.project.backend.service;

import TQS.project.backend.dto.StationDTO;
import TQS.project.backend.entity.Charger;
import TQS.project.backend.entity.Station;
import TQS.project.backend.repository.StationRepository;
import TQS.project.backend.repository.ChargerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StationService {
  private final StationRepository stationRepository;
  private final ChargerRepository chargerRepository;

  @Autowired
  public StationService(StationRepository stationRepository, ChargerRepository chargerRepository) {
    this.stationRepository = stationRepository;
    this.chargerRepository = chargerRepository;
  }

  public Station createStation(StationDTO dto) {
    Station station = new Station();
    station.setName(dto.getName());
    station.setBrand(dto.getBrand());
    station.setLatitude(dto.getLatitude());
    station.setLongitude(dto.getLongitude());
    station.setAddress(dto.getAddress());
    station.setNumberOfChargers(dto.getNumberOfChargers());
    station.setOpeningHours(dto.getOpeningHours());
    station.setClosingHours(dto.getClosingHours());
    station.setPrice(dto.getPrice());
    return stationRepository.save(station);
  }

  public List<Station> getAllStations() {
    return stationRepository.findAll();
  }

  public Optional<Station> getStationById(Long id) {
    return stationRepository.findById(id);
  }

  public List<Station> searchStations(
      String district,
      Double maxPrice,
      String chargerType,
      Double minPower,
      Double maxPower,
      String connectorType,
      Boolean available) {

    List<Long> stationIdsFromChargers =
        chargerRepository.findAll().stream()
            .filter(c -> chargerType == null || chargerType.equalsIgnoreCase(c.getType()))
            .filter(c -> minPower == null || c.getPower() >= minPower)
            .filter(c -> maxPower == null || c.getPower() <= maxPower)
            .filter(
                c -> connectorType == null || connectorType.equalsIgnoreCase(c.getConnectorType()))
            .filter(c -> available == null || c.getAvailable().equals(available))
            .map(c -> c.getStation().getId())
            .distinct()
            .toList();

    return stationRepository.findAll().stream()
        .filter(
            s -> district == null || s.getAddress().toLowerCase().contains(district.toLowerCase()))
        .filter(s -> maxPrice == null || s.getPrice() <= maxPrice)
        .filter(s -> stationIdsFromChargers.contains(s.getId()))
        .toList();
  }

  public List<Charger> getAllStationChargers(long id) {
    return chargerRepository.findAllByStationId(id);
  }

  public Station updateStation(Long id, StationDTO dto) {
    Station station =
        stationRepository.findById(id).orElseThrow(() -> new RuntimeException("Station not found"));

    station.setName(dto.getName());
    station.setAddress(dto.getAddress());
    station.setAddress(dto.getAddress());
    station.setLatitude(dto.getLatitude());
    station.setLongitude(dto.getLongitude());
    station.setPrice(dto.getPrice());
    station.setClosingHours(dto.getClosingHours());
    station.setOpeningHours(dto.getOpeningHours());

    return stationRepository.save(station);
  }
}
