package TQS.project.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import TQS.project.backend.dto.AssignStationDTO;
import TQS.project.backend.dto.CreateStaffDTO;
import TQS.project.backend.entity.Staff;
import TQS.project.backend.entity.Station;
import TQS.project.backend.service.StaffService;
import TQS.project.backend.security.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

  @Autowired private StaffService staffService;

  @Autowired private JwtProvider jwtProvider;

  @Operation(summary = "Create a new operator staff account.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Operator account created successfully.",
            content =
                @Content(schema = @Schema(example = "Operator account created successfully."))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data or operator creation failed.",
            content = @Content(schema = @Schema(example = "Error message describing the failure")))
      })
  @PostMapping("/operator")
  public ResponseEntity<?> createOperator(@Valid @RequestBody CreateStaffDTO dto) {
    staffService.createOperator(dto);
    return ResponseEntity.ok("Operator account created successfully.");
  }

  @Operation(summary = "Retrieve a list of all operator staff.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of operators retrieved successfully.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Staff.class)))
      })
  @GetMapping("/operators")
  public ResponseEntity<List<Staff>> getAllOperators() {
    List<Staff> operators = staffService.getAllOperators();
    return ResponseEntity.ok(operators);
  }

  @Operation(summary = "Assign a station to an operator.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Station assigned to operator successfully.",
            content =
                @Content(schema = @Schema(example = "Station assigned to operator successfully."))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input or assignment failed.",
            content = @Content(schema = @Schema(example = "Invalid operator or station ID.")))
      })
  @PostMapping("/operator/assign-station")
  public ResponseEntity<?> assignStationToOperator(@Valid @RequestBody AssignStationDTO dto) {
    staffService.assignStationToOperator(dto);
    return ResponseEntity.ok("Station assigned to operator successfully.");
  }

  @Operation(summary = "Get the station assigned to the currently authenticated operator.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Station retrieved successfully.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Station.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - missing or invalid token.",
            content = @Content(schema = @Schema(example = "Unauthorized")))
      })
  @GetMapping("/station")
  public ResponseEntity<Station> getMyStation(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    String token = authHeader.substring(7);

    // Use your JwtProvider to extract the email from the JWT
    String email = jwtProvider.getEmailFromToken(token);

    // Service call: use the email as a secure, verified source of identity
    Station station = staffService.getStationForOperator(email);
    return ResponseEntity.ok(station);
  }
}
