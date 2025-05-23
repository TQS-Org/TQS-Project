package TQS.project.backend;

import TQS.project.backend.controller.AuthController;
import TQS.project.backend.dto.LoginRequest;
import TQS.project.backend.dto.LoginResponse;
import TQS.project.backend.dto.RegisterRequest;
import TQS.project.backend.security.JwtAuthFilter;
import TQS.project.backend.security.JwtProvider;
import TQS.project.backend.service.AuthService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;

  @SuppressWarnings("removal")
  @MockBean
  private JwtProvider jwtProvider;

  @SuppressWarnings("removal")
  @MockBean
  private JwtAuthFilter jwtAuthFilter;

  @SuppressWarnings("removal")
  @MockBean
  private AuthService authService;

  @Test
  @Requirement("SCRUM-41")
  void loginAsEvDriver_returnsTokenAndEvDriverRole() throws Exception {
    LoginRequest request = new LoginRequest("driver@mail.com", "password");
    LoginResponse response = new LoginResponse("mocked-jwt", "EV_DRIVER");

    when(authService.login(any())).thenReturn(response);

    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("mocked-jwt"))
        .andExpect(jsonPath("$.role").value("EV_DRIVER"));
  }

  @Test
  @Requirement("SCRUM-41")
  void validateToken_withValidToken_returnsRole() throws Exception {
    String token = "valid-jwt-token";
    String role = "EV_DRIVER";

    // Mock the JwtProvider to return the expected role
    when(jwtProvider.getRoleFromToken(token)).thenReturn(role);

    // Perform the GET request with Authorization header
    mockMvc
        .perform(get("/api/auth/validate").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.role").value(role));
  }

  @Test
  @Requirement("SCRUM-41")
  void validateToken_withMissingAuthorizationHeader_returnsUnauthorized() throws Exception {
    mockMvc.perform(get("/api/auth/validate")).andExpect(status().isUnauthorized());
  }

  @Test
  @Requirement("SCRUM-147")
  void registerAsEvDriver_returnsTokenAndEvDriverRole() throws Exception {
    RegisterRequest request = new RegisterRequest();
    request.setName("New Driver");
    request.setPassword("newpass123");
    request.setAge(28);
    request.setMail("newdriver@mail.com");
    request.setNumber("911222333");

    LoginResponse mockResponse = new LoginResponse("new-token", "EV_DRIVER");

    when(authService.register(any())).thenReturn(mockResponse);

    mockMvc
        .perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("new-token"))
        .andExpect(jsonPath("$.role").value("EV_DRIVER"));
  }
}
