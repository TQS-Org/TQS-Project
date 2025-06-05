package TQS.project.backend.controller;

import TQS.project.backend.dto.LoginRequest;
import TQS.project.backend.dto.LoginResponse;
import TQS.project.backend.dto.RegisterRequest;
import TQS.project.backend.security.JwtProvider;
import TQS.project.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

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

  @Operation(summary = "Authenticate an account and log in.")
  @ApiResponse(
      responseCode = "200",
      description = "Successfully authenticated and logged user in.",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = LoginResponse.class)))
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    return ResponseEntity.ok(authService.login(request));
  }

  @Operation(summary = "Validate JWT token and return the user's role.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Token is valid.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{\"role\": \"USER\"}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Missing or invalid token.",
            content = @Content)
      })
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

  @Operation(summary = "Register a new account.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Account registered successfully and user logged in.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data.", content = @Content)
      })
  @PostMapping("/register")
  public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
    return ResponseEntity.ok(authService.register(request));
  }
}
