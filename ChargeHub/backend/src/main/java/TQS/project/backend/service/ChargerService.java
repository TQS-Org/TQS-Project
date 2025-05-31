package TQS.project.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import TQS.project.backend.dto.ChargerDTO;
import TQS.project.backend.entity.Charger;
import TQS.project.backend.entity.Station;
import TQS.project.backend.repository.ChargerRepository;
import TQS.project.backend.repository.StationRepository;

import java.util.List;

@Service
public class ChargerService {

    @Autowired
    private ChargerRepository chargerRepository;

    @Autowired
    private StationRepository stationRepository;

    public Charger createCharger(ChargerDTO dto) {
        Station station = stationRepository.findById(dto.getStationId())
                .orElseThrow(() -> new RuntimeException("Station not found"));

        Charger charger = new Charger(dto.getType(), dto.getPower(), dto.getAvailable(), dto.getConnectorType());
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
        Station station = stationRepository.findById(dto.getStationId())
                .orElseThrow(() -> new RuntimeException("Station not found"));

        charger.setType(dto.getType());
        charger.setPower(dto.getPower());
        charger.setAvailable(dto.getAvailable());
        charger.setConnectorType(dto.getConnectorType());
        charger.setStation(station);
        return chargerRepository.save(charger);
    }

    public void deleteCharger(Long id) {
        chargerRepository.deleteById(id);
    }
}
