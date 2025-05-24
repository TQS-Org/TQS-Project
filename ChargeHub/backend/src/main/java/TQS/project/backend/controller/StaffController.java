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
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

  @Autowired private StaffService staffService;

  @PostMapping("/operator")
  public ResponseEntity<?> createOperator(@Valid @RequestBody CreateStaffDTO dto) {
    try {
      staffService.createOperator(dto);
      return ResponseEntity.ok("Operator account created successfully.");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @GetMapping("/operators")
  public ResponseEntity<List<Staff>> getAllOperators() {
    List<Staff> operators = staffService.getAllOperators();
    return ResponseEntity.ok(operators);
  }
}
