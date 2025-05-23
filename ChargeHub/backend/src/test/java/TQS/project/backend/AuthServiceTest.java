package TQS.project.backend;

import TQS.project.backend.entity.Client;
import TQS.project.backend.entity.Staff;
import TQS.project.backend.entity.Role;
import TQS.project.backend.dto.LoginRequest;
import TQS.project.backend.dto.LoginResponse;
import TQS.project.backend.dto.RegisterRequest;
import TQS.project.backend.repository.ClientRepository;
import TQS.project.backend.repository.StaffRepository;
import TQS.project.backend.security.JwtProvider;
import TQS.project.backend.service.AuthService;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock private ClientRepository clientRepo;

  @Mock private StaffRepository staffRepo;

  @Mock private JwtProvider jwtProvider;

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private AuthService authService;

  @Test
  @Requirement("SCRUM-41")
  void loginAsClient_withValidPassword_returnsTokenAndEvDriverRole() {
    Client client = new Client();
    client.setMail("driver@mail.com");
    client.setPassword("hashed-password");

    when(clientRepo.findByMail("driver@mail.com")).thenReturn(Optional.of(client));
    when(passwordEncoder.matches("raw-password", "hashed-password")).thenReturn(true);
    when(jwtProvider.generateToken("driver@mail.com", "EV_DRIVER")).thenReturn("mocked-token");

    LoginRequest request = new LoginRequest("driver@mail.com", "raw-password");
    LoginResponse response = authService.login(request);

    assertEquals("mocked-token", response.getToken());
    assertEquals("EV_DRIVER", response.getRole());
  }

  @Test
  @Requirement("SCRUM-41")
  void loginAsStaff_withValidPassword_returnsTokenAndStaffRole() {
    Staff staff = new Staff();
    staff.setMail("operator@mail.com");
    staff.setPassword("hashed");
    staff.setRole(Role.OPERATOR);

    when(staffRepo.findByMail("operator@mail.com")).thenReturn(Optional.of(staff));
    when(passwordEncoder.matches("raw", "hashed")).thenReturn(true);
    when(jwtProvider.generateToken("operator@mail.com", "OPERATOR")).thenReturn("token-op");

    LoginRequest request = new LoginRequest("operator@mail.com", "raw");
    LoginResponse response = authService.login(request);

    assertEquals("token-op", response.getToken());
    assertEquals("OPERATOR", response.getRole());
  }

  @Test
  @Requirement("SCRUM-41")
  void login_withInvalidPassword_throwsException() {
    Client client = new Client();
    client.setMail("fail@mail.com");
    client.setPassword("hashed");

    when(clientRepo.findByMail("fail@mail.com")).thenReturn(Optional.of(client));
    when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

    LoginRequest request = new LoginRequest("fail@mail.com", "wrong");

    assertThrows(RuntimeException.class, () -> authService.login(request));
  }

  @Test
  @Requirement("SCRUM-41")
  void login_withUnknownEmail_throwsException() {
    when(clientRepo.findByMail("ghost@mail.com")).thenReturn(Optional.empty());
    when(staffRepo.findByMail("ghost@mail.com")).thenReturn(Optional.empty());

    LoginRequest request = new LoginRequest("ghost@mail.com", "any");

    assertThrows(RuntimeException.class, () -> authService.login(request));
  }

  @Test
  @Requirement("SCRUM-147")
  void register_withValidInput_returnsToken() {
    RegisterRequest req = new RegisterRequest();
    req.setName("New Driver");
    req.setPassword("rawpass123");
    req.setAge(30);
    req.setMail("driver@mail.com");
    req.setNumber("912345678");

    when(clientRepo.findByMail("driver@mail.com")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("rawpass123")).thenReturn("hashedpass");
    when(jwtProvider.generateToken("driver@mail.com", "EV_DRIVER")).thenReturn("mocked-jwt");

    LoginResponse response = authService.register(req);

    assertEquals("mocked-jwt", response.getToken());
    assertEquals("EV_DRIVER", response.getRole());
  }

  @Test
  @Requirement("SCRUM-147")
  void register_withExistingEmail_throwsException() {
    Client existing = new Client();
    existing.setMail("existing@mail.com");

    when(clientRepo.findByMail("existing@mail.com")).thenReturn(Optional.of(existing));

    RegisterRequest req = new RegisterRequest();
    req.setMail("existing@mail.com");

    assertThrows(RuntimeException.class, () -> authService.register(req));
  }
}
