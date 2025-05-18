package TQS.project.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import TQS.project.backend.repository.ClientRepository;
import TQS.project.backend.repository.StaffRepository;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class BackendApplicationTests {

	@SuppressWarnings("removal")
	@MockBean
	private ClientRepository clientRepository;

	@SuppressWarnings("removal")
	@MockBean
	private StaffRepository staffRepository;

	@Test
	void contextLoads() {
	}

}
