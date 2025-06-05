package TQS.project.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import TQS.project.backend.dto.CreateStaffDTO;
import TQS.project.backend.entity.Staff;
import TQS.project.backend.service.StaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

  @Autowired private StaffService staffService;

  @Operation(summary = "Create a new operator staff account.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Operator account created successfully.",
          content = @Content(schema = @Schema(example = "Operator account created successfully."))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid input data or operator creation failed.",
          content = @Content(schema = @Schema(example = "Error message describing the failure"))
      )
  })
  @PostMapping("/operator")
  public ResponseEntity<?> createOperator(@Valid @RequestBody CreateStaffDTO dto) {
    try {
      staffService.createOperator(dto);
      return ResponseEntity.ok("Operator account created successfully.");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @Operation(summary = "Retrieve a list of all operator staff.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "List of operators retrieved successfully.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = Staff.class)
          )
      )
  })
  @GetMapping("/operators")
  public ResponseEntity<List<Staff>> getAllOperators() {
    List<Staff> operators = staffService.getAllOperators();
    return ResponseEntity.ok(operators);
  }
}
