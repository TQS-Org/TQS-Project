package TQS.project.backend.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Component
public class EncodePassword implements CommandLineRunner {

    @Override
    public void run(String... args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println("Encoded driverpass: " + encoder.encode("driverpass"));
        System.out.println("operatorpass: " + encoder.encode("operatorpass"));
        System.out.println("adminpass: " + encoder.encode("adminpass"));
    }
}
