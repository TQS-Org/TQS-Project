package TQS.project.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import TQS.project.backend.dto.ChargerDTO;
import TQS.project.backend.entity.Charger;
import TQS.project.backend.entity.Station;
import TQS.project.backend.repository.ChargerRepository;
import TQS.project.backend.repository.StationRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ChargerService {

  @Autowired
  private ChargerRepository chargerRepository;

  @Autowired
  private StationRepository stationRepository;

  @Autowired
  public ChargerService(ChargerRepository chargerRepository, StationRepository stationRepository) {
    this.chargerRepository = chargerRepository;
    this.stationRepository = stationRepository;
  }

  public Optional<Charger> getChargerById(Long id) {
    return chargerRepository.findById(id);
  }

  public Charger createChargerForStation(Long stationId, ChargerDTO chargerDTO) {
    Station station = stationRepository.findById(stationId)
        .orElseThrow(() -> new RuntimeException("Station not found with ID: " + stationId));

    Charger charger = new Charger();
    charger.setType(chargerDTO.getType());
    charger.setConnectorType(chargerDTO.getConnectorType());
    charger.setPower(chargerDTO.getPower());
    charger.setAvailable(chargerDTO.getAvailable());
    charger.setStation(station);

    return chargerRepository.save(charger);
  }

  public Charger getCharger(Long id) {
    return chargerRepository.findById(id).orElseThrow(() -> new RuntimeException("Charger not found"));
  }

  public List<Charger> getChargersByStation(Long stationId) {
    Station station = stationRepository.findById(stationId)
        .orElseThrow(() -> new RuntimeException("Station not found"));
    return chargerRepository.findByStation(station);
  }

  public Charger updateCharger(Long id, ChargerDTO dto) {
    Charger charger = getCharger(id);

    charger.setType(dto.getType());
    charger.setPower(dto.getPower());
    charger.setAvailable(dto.getAvailable());
    charger.setConnectorType(dto.getConnectorType());
    return chargerRepository.save(charger);
  }
}
