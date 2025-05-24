package TQS.project.backend.controller;

import TQS.project.backend.entity.Station;
import TQS.project.backend.service.StationService;
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

  @GetMapping
  public ResponseEntity<List<Station>> getAllStations() {
    List<Station> stations = stationService.getAllStations();
    return ResponseEntity.ok(stations);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Station> getStationById(@PathVariable Long id) {
    Optional<Station> station = stationService.getStationById(id);
    return station.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

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
}
