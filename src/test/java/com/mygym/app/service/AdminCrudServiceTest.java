package com.mygym.app.service;

import com.mygym.app.models.Admin;
import com.mygym.app.models.AdminWrapper;
import com.mygym.app.repository.AdminRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCrudServiceTest {

    @Mock
    private AdminRepository repository;

    @Mock
    private IdGenerationService idGenerationService;

    @InjectMocks
    private AdminCrudService adminCrudService;

    @Test
    @DisplayName("Should return existing admin with ID '-1' if duplicate credentials found")
    void testCreateAdmin_DuplicateExists_ReturnsAdminWithMinusOneId() {
        Admin inputAdmin = new Admin();
        inputAdmin.setUsername("john_admin");
        inputAdmin.setMobileNumber("9876543210");
        inputAdmin.setEmailAddress("john@gym.com");

        Admin existing = new Admin();
        existing.setUsername("john_admin");

        when(repository.findByUsernameOrMobileOrEmail("john_admin", "9876543210", "john@gym.com"))
                .thenReturn(Optional.of(existing));

        Admin result = adminCrudService.createAdmin(inputAdmin);

        assertEquals("-1", result.getAdminId());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should assign ID '0' when creating primary admin account")
    void testCreateAdmin_PrimaryAdmin_AssignsIdZero() {
        Admin inputAdmin = new Admin();
        inputAdmin.setUsername("admin");

        when(repository.findByUsernameOrMobileOrEmail(any(), any(), any())).thenReturn(Optional.empty());
        when(repository.save(any(Admin.class))).thenAnswer(i -> i.getArgument(0));

        Admin result = adminCrudService.createAdmin(inputAdmin);

        assertEquals("0", result.getAdminId());
        verify(idGenerationService, never()).getAdminId();
    }

    @Test
    @DisplayName("Should fetch generated ID from IdGenerationService for standard admin")
    void testCreateAdmin_StandardAdmin_GeneratesNewId() {
        Admin inputAdmin = new Admin();
        inputAdmin.setUsername("branch_admin");

        when(repository.findByUsernameOrMobileOrEmail(any(), any(), any())).thenReturn(Optional.empty());
        when(idGenerationService.getAdminId()).thenReturn(101L);
        when(repository.save(any(Admin.class))).thenAnswer(i -> i.getArgument(0));

        Admin result = adminCrudService.createAdmin(inputAdmin);

        assertEquals("101", result.getAdminId());
        verify(idGenerationService, times(1)).incrementAdminId();
    }

    @Test
    @DisplayName("Should return inactive admins wrapper when inactive records exist")
    void testFindInactiveAdmins_RecordsFound_ReturnsWrapper() {
        Admin admin1 = new Admin();
        admin1.setAdminId("101");

        when(repository.findInactiveAdmins()).thenReturn(List.of(admin1));

        AdminWrapper result = adminCrudService.findInactiveAdmins();

        assertNotNull(result);
        verify(repository, times(1)).findInactiveAdmins();
    }

    @Test
    @DisplayName("Should set isActive to true on updateAdminStatus")
    void testUpdateAdminStatus_Success() {
        Admin admin = new Admin();
        admin.setAdminId("101");
        admin.setIsActive(false);

        when(repository.findByAdminId("101")).thenReturn(Optional.of(admin));

        Boolean status = adminCrudService.updateAdminStatus("101");

        assertTrue(status);
        assertTrue(admin.getIsActive());
        verify(repository, times(1)).save(admin);
    }
}