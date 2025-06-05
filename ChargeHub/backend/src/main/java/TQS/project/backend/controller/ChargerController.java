package TQS.project.backend.controller;

import TQS.project.backend.entity.Charger;
import TQS.project.backend.service.ChargerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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
}
