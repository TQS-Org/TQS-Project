package TQS.project.backend.service;

import TQS.project.backend.dto.LoginRequest;
import TQS.project.backend.dto.LoginResponse;
import TQS.project.backend.dto.RegisterRequest;
import TQS.project.backend.repository.ClientRepository;
import TQS.project.backend.repository.StaffRepository;
import TQS.project.backend.security.JwtProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import TQS.project.backend.entity.Client;

@Service
public class AuthService {

  @Autowired private ClientRepository clientRepo;

  @Autowired private StaffRepository staffRepo;

  @Autowired private JwtProvider jwtProvider;

  @Autowired private PasswordEncoder passwordEncoder;

  public LoginResponse login(LoginRequest request) {
    String email = request.getEmail();
    String rawPassword = request.getPassword();

    // Try Client login
    return clientRepo
        .findByMail(email)
        .filter(client -> passwordEncoder.matches(rawPassword, client.getPassword()))
        .map(
            client -> new LoginResponse(jwtProvider.generateToken(email, "EV_DRIVER"), "EV_DRIVER"))

        // If not a client, try Staff login
        .orElseGet(
            () ->
                staffRepo
                    .findByMail(email)
                    .filter(staff -> passwordEncoder.matches(rawPassword, staff.getPassword()))
                    .map(
                        staff -> {
                          String role = staff.getRole().name();
                          return new LoginResponse(jwtProvider.generateToken(email, role), role);
                        })
                    .orElseThrow(() -> new RuntimeException("Invalid email or password")));
  }

  public LoginResponse register(RegisterRequest request) {
    if (clientRepo.findByMail(request.getMail()).isPresent()) {
      throw new RuntimeException("Email already in use");
    }

    Client newClient = new Client();
    newClient.setName(request.getName());
    newClient.setPassword(passwordEncoder.encode(request.getPassword()));
    newClient.setAge(request.getAge());
    newClient.setMail(request.getMail());
    newClient.setNumber(request.getNumber());

    clientRepo.save(newClient);

    String token = jwtProvider.generateToken(newClient.getMail(), "EV_DRIVER");

    return new LoginResponse(token, "EV_DRIVER");
  }
}
