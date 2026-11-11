package com.mygym.app.contract;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.mygym.app.config.PasswordRedactingAdvice;
import com.mygym.app.controller.AdminCrudController;
import com.mygym.app.models.Admin;
import com.mygym.app.service.AdminCrudService;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

public abstract class ContractVerifierBase {

    @BeforeEach
    void setUp() {
        AdminCrudService service = mock(AdminCrudService.class);
        Admin stored = new Admin(null, "7", "Alex Rao", "admin.alex", "$2a$10$storedBcryptHashValue", "9876543210",
                "alex@mygym.app", true, "3");
        when(service.authenticate(anyString(), anyString())).thenReturn(Admin.empty());
        when(service.authenticate("admin.alex", "S3cret!pass")).thenReturn(stored);
        when(service.getAdminByAdminId("7")).thenReturn(stored);
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.standaloneSetup(new AdminCrudController(service))
                .setControllerAdvice(new PasswordRedactingAdvice())
                .build());
    }
}
