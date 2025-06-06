package TQS.project.backend.controller;

import TQS.project.backend.entity.Charger;
import TQS.project.backend.dto.StationDTO;
import TQS.project.backend.entity.Station;
import TQS.project.backend.service.StationService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/stations")
public class StationController {

  private final StationService stationService;

  public StationController(StationService stationService) {
    this.stationService = stationService;
  }

  @Operation(summary = "Create a new station.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Station created successfully.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Station.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid station data provided.",
            content = @Content)
      })
  @PostMapping
  public ResponseEntity<Station> createStation(@Valid @RequestBody StationDTO stationDTO) {
    Station station = stationService.createStation(stationDTO);
    return ResponseEntity.ok(station);
  }

  @Operation(summary = "Get a list of all stations.")
  @ApiResponse(
      responseCode = "200",
      description = "List of stations retrieved successfully.",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = Station.class)))
  @GetMapping
  public ResponseEntity<List<Station>> getAllStations() {
    List<Station> stations = stationService.getAllStations();
    return ResponseEntity.ok(stations);
  }

  @Operation(summary = "Get a station by its ID.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Station found and returned.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Station.class))),
        @ApiResponse(responseCode = "404", description = "Station not found.", content = @Content)
      })
  @GetMapping("/{id}")
  public ResponseEntity<Station> getStationById(@PathVariable Long id) {
    Optional<Station> station = stationService.getStationById(id);
    return station.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Operation(
      summary =
          "Search stations by optional filters: district, max price, charger type, power range,"
              + " connector type, availability.")
  @ApiResponse(
      responseCode = "200",
      description = "Filtered list of stations returned.",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = Station.class)))
  @GetMapping("/search")
  public ResponseEntity<List<Station>> searchStations(
      @RequestParam(required = false) String district,
      @RequestParam(required = false) Double maxPrice,
      @RequestParam(required = false) String chargerType,
      @RequestParam(required = false) Double minPower,
      @RequestParam(required = false) Double maxPower,
      @RequestParam(required = false) String connectorType,
      @RequestParam(required = false) Boolean available) {

    List<Station> results =
        stationService.searchStations(
            district, maxPrice, chargerType, minPower, maxPower, connectorType, available);

    return ResponseEntity.ok(results);
  }

  @Operation(summary = "Get all chargers associated with a station.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of chargers returned.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Charger.class))),
        @ApiResponse(responseCode = "404", description = "Station not found.", content = @Content)
      })
  @GetMapping("/{id}/chargers")
  public ResponseEntity<List<Charger>> getStationChargers(@PathVariable Long id) {
    List<Charger> chargers = stationService.getAllStationChargers(id);
    return ResponseEntity.ok(chargers);
  }

  @Operation(summary = "Update an existing station by ID.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Station updated successfully.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Station.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data.", content = @Content),
        @ApiResponse(responseCode = "404", description = "Station not found.", content = @Content)
      })
  @PutMapping("/{id}")
  public ResponseEntity<Station> updateStation(
      @PathVariable Long id, @Valid @RequestBody StationDTO dto) {

    Station updatedStation = stationService.updateStation(id, dto);
    return ResponseEntity.ok(updatedStation);
  }
}
