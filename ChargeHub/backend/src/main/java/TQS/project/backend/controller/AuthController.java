package TQS.project.backend.controller;

import TQS.project.backend.dto.LoginRequest;
import TQS.project.backend.dto.LoginResponse;
import TQS.project.backend.security.JwtProvider;
import TQS.project.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  private final JwtProvider jwtProvider;

  public AuthController(AuthService authService, JwtProvider jwtProvider) {
    this.authService = authService;
    this.jwtProvider = jwtProvider;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    return ResponseEntity.ok(authService.login(request));
  }

  @GetMapping("/validate")
  public ResponseEntity<Map<String, String>> validateToken(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      String role = jwtProvider.getRoleFromToken(token);
      return ResponseEntity.ok(Map.of("role", role));
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }
}
