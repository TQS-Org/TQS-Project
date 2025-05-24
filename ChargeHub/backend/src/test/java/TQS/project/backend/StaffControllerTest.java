package TQS.project.backend;

import TQS.project.backend.controller.StaffController;
import TQS.project.backend.dto.CreateStaffDTO;
import TQS.project.backend.entity.Staff;
import TQS.project.backend.security.JwtAuthFilter;
import TQS.project.backend.security.JwtProvider;
import TQS.project.backend.service.StaffService;
import TQS.project.backend.entity.Role;
import java.util.List;
import TQS.project.backend.Config.TestSecurityConfig;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestSecurityConfig.class)
@WebMvcTest(StaffController.class)
@AutoConfigureMockMvc(addFilters = false)
public class StaffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("removal")
    @MockBean
    private StaffService staffService;

    @SuppressWarnings("removal")
    @MockBean
    private JwtProvider jwtProvider;

    @SuppressWarnings("removal")
    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    @Requirement("SCRUM-35")
    void testCreateOperator_success() throws Exception {
        CreateStaffDTO dto = new CreateStaffDTO();
        dto.name = "New Operator";
        dto.mail = "newoperator@mail.com";
        dto.password = "secure123";
        dto.age = 30;
        dto.number = "999888777";
        dto.address = "Setúbal";

        doNothing().when(staffService).createOperator(dto);

        mockMvc.perform(post("/api/staff/operator")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Operator account created successfully."));
    }

    @Test
    @Requirement("SCRUM-35")
    void testCreateOperator_duplicateEmail() throws Exception {
        CreateStaffDTO dto = new CreateStaffDTO();
        dto.name = "Duplicate";
        dto.mail = "existing@mail.com";
        dto.password = "pass";
        dto.age = 25;
        dto.number = "123456789";
        dto.address = "Lisbon";

        doThrow(new IllegalArgumentException("Email already in use"))
                .when(staffService)
                .createOperator(any(CreateStaffDTO.class));

        mockMvc.perform(post("/api/staff/operator")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email already in use"));
    }

    @Test
    @Requirement("SCRUM-35")
    void testGetAllOperators_returnsList() throws Exception {
        Staff s1 = new Staff();
        s1.setId(1L);
        s1.setName("Operator One");
        s1.setMail("op1@mail.com");
        s1.setRole(Role.OPERATOR);

        Staff s2 = new Staff();
        s2.setId(2L);
        s2.setName("Operator Two");
        s2.setMail("op2@mail.com");
        s2.setRole(Role.OPERATOR);

        when(staffService.getAllOperators()).thenReturn(List.of(s1, s2));

        mockMvc.perform(get("/api/staff/operators"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Operator One"))
                .andExpect(jsonPath("$[1].mail").value("op2@mail.com"));
    }

}
