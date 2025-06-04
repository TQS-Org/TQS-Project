package TQS.project.backend.controller;

import TQS.project.backend.entity.Charger;
import TQS.project.backend.service.ChargerService;
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
}
