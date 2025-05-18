package TQS.project.backend.service;

import TQS.project.backend.dto.LoginRequest;
import TQS.project.backend.dto.LoginResponse;
import TQS.project.backend.entity.Client;
import TQS.project.backend.entity.Staff;
import TQS.project.backend.repository.ClientRepository;
import TQS.project.backend.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private ClientRepository clientRepo;

    @Autowired
    private StaffRepository staffRepo;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        String email = request.getEmail();
        String rawPassword = request.getPassword();

        // Try Client login
        return clientRepo.findByMail(email)
                .filter(client -> passwordEncoder.matches(rawPassword, client.getPassword()))
                .map(client -> new LoginResponse(jwtProvider.generateToken(email, "EV_DRIVER"), "EV_DRIVER"))

                // If not a client, try Staff login
                .orElseGet(() -> staffRepo.findByMail(email)
                        .filter(staff -> passwordEncoder.matches(rawPassword, staff.getPassword()))
                        .map(staff -> {
                            String role = staff.getRole().name();
                            return new LoginResponse(jwtProvider.generateToken(email, role), role);
                        })
                        .orElseThrow(() -> new RuntimeException("Invalid email or password")));
    }
}
