package TQS.project.backend.controller;

import TQS.project.backend.dto.ChargerDTO;
import TQS.project.backend.entity.Charger;
import TQS.project.backend.service.ChargerService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

import TQS.project.backend.dto.ChargerTokenDTO;

@RestController
@RequestMapping("/api/charger")
public class ChargerController {

  private final ChargerService chargerService;

  public ChargerController(ChargerService chargerService) {
    this.chargerService = chargerService;
  }

  @Operation(summary = "Retrieve charger details by its ID.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Charger found and returned successfully.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Charger.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Charger not found with the given ID.",
            content = @Content)
      })
  @GetMapping("/{id}")
  public ResponseEntity<Charger> getChargerById(@PathVariable Long id) {
    Optional<Charger> charger = chargerService.getChargerById(id);
    return charger.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping("/{stationId}")
  public ResponseEntity<Charger> createChargerForStation(
      @PathVariable Long stationId, @Valid @RequestBody ChargerDTO chargerDTO) {

    Charger createdCharger = chargerService.createChargerForStation(stationId, chargerDTO);

    return ResponseEntity.ok(createdCharger);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Charger> updateCharger(
      @PathVariable Long id, @Valid @RequestBody ChargerDTO dto) {

    Charger updatedCharger = chargerService.updateCharger(id, dto);
    return ResponseEntity.ok(updatedCharger);
  }

  @PostMapping("/{id}/session")
  public ResponseEntity<?> createChargingSession(
      @PathVariable("id") long chargerId, @RequestBody ChargerTokenDTO request) {
    try {
      chargerService.startChargingSession(request.getChargeToken(), chargerId);
      return ResponseEntity.ok("Charger unlocked successfully, charge session starting...");
    } catch (IllegalArgumentException | IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
