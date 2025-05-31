package TQS.project.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import TQS.project.backend.dto.ChargerDTO;
import TQS.project.backend.entity.Charger;
import TQS.project.backend.service.ChargerService;

import java.util.List;

@RestController
@RequestMapping("api/chargers")
public class ChargerController {

    @Autowired
    private ChargerService chargerService;

    @PostMapping
    public ResponseEntity<Charger> createCharger(@Valid @RequestBody ChargerDTO chargerDTO) {
        Charger charger = chargerService.createCharger(chargerDTO);
        return ResponseEntity.ok(charger);
    }

    @GetMapping("/station/{stationId}")
    public ResponseEntity<List<Charger>> getChargersByStation(@PathVariable Long stationId) {
        return ResponseEntity.ok(chargerService.getChargersByStation(stationId));
    }
}
