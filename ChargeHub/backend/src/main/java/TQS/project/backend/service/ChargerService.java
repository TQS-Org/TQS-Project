package TQS.project.backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import TQS.project.backend.entity.Charger;
import TQS.project.backend.repository.ChargerRepository;

@Service
public class ChargerService {

    private ChargerRepository chargerRepository;

    @Autowired
    public ChargerService(ChargerRepository chargerRepository) {
        this.chargerRepository = chargerRepository;
    }

    public Optional<Charger> getChargerById(Long id) {
        return chargerRepository.findById(id);
    }
}
