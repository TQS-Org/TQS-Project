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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

import TQS.project.backend.dto.ChargerTokenDTO;
import TQS.project.backend.dto.FinishedChargingSessionDTO;

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

  @Operation(summary = "Create a new charger for a given station.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Charger created successfully.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Charger.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data or station not found.",
            content = @Content)
      })
  @PostMapping("/{stationId}")
  public ResponseEntity<Charger> createChargerForStation(
      @PathVariable Long stationId, @Valid @RequestBody ChargerDTO chargerDTO) {

    Charger createdCharger = chargerService.createChargerForStation(stationId, chargerDTO);

    return ResponseEntity.ok(createdCharger);
  }

  @Operation(summary = "Update an existing charger.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Charger updated successfully.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Charger.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input or charger not found.",
            content = @Content)
      })
  @PutMapping("/{id}")
  public ResponseEntity<Charger> updateCharger(
      @PathVariable Long id, @Valid @RequestBody ChargerDTO dto) {

    Charger updatedCharger = chargerService.updateCharger(id, dto);
    return ResponseEntity.ok(updatedCharger);
  }

  @Operation(summary = "Start a charging session using a charge token.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Charging session started successfully.",
            content =
                @Content(
                    schema =
                        @Schema(
                            example =
                                "Charger unlocked successfully, charge session starting..."))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid token or charger state not suitable for charging.",
            content = @Content(schema = @Schema(example = "Error message describing failure")))
      })
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

  @Operation(summary = "Finish an ongoing charging session by providing energy and end time.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Charging session successfully concluded.",
        content = @Content(schema = @Schema(example = "Charging session successfully concluded."))),
    @ApiResponse(
        responseCode = "404",
        description = "Charging session or charger not found.",
        content = @Content(schema = @Schema(example = "Session not found."))),
    @ApiResponse(
        responseCode = "500",
        description = "Unexpected error while updating the session.",
        content = @Content(schema = @Schema(example = "Error updating charging session.")))
  })
  @PutMapping("/{id}/session/{sessionId}")
  public ResponseEntity<?> finishChargingSession(
      @PathVariable Long id,
      @PathVariable Long sessionId,
      @RequestBody FinishedChargingSessionDTO request) {
    try {
      chargerService.finishChargingSession(sessionId, request);
      return ResponseEntity.ok("Charging session successfully concluded.");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error updating charging session.");
    }
  }
}
