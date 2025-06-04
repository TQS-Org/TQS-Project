package TQS.project.backend.controller;

import TQS.project.backend.dto.ChargerDTO;
import TQS.project.backend.entity.Charger;
import TQS.project.backend.service.ChargerService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/charger")
public class ChargerController {

  private final ChargerService chargerService;

  public ChargerController(ChargerService chargerService) {
    this.chargerService = chargerService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<Charger> getChargerById(@PathVariable Long id) {
    Optional<Charger> charger = chargerService.getChargerById(id);
    return charger.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping("/{stationId}")
  public ResponseEntity<Charger> createChargerForStation(
      @PathVariable Long stationId,
      @Valid @RequestBody ChargerDTO chargerDTO) {

    Charger createdCharger = chargerService.createChargerForStation(stationId, chargerDTO);

    return ResponseEntity.ok(createdCharger);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Charger> updateCharger(
      @PathVariable Long id,
      @Valid @RequestBody ChargerDTO dto) {

    Charger updatedCharger = chargerService.updateCharger(id, dto);
    return ResponseEntity.ok(updatedCharger);
  }
}
