package com.mygym.app.service;

import static com.mygym.app.service.PeopleFixtures.admin;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mygym.app.models.Admin;
import com.mygym.app.models.AdminWrapper;
import com.mygym.app.repository.AdminRepository;

@ExtendWith(MockitoExtension.class)
class AdminCrudServiceTest {

    @Mock
    private AdminRepository repository;

    @Mock
    private IdGenerationService idGenerationService;

    @Spy
    private PasswordService passwordService = new PasswordService();

    @InjectMocks
    private AdminCrudService adminCrudService;

    @Test
    @DisplayName("Should return existing admin with ID '-1' if duplicate credentials found")
    void testCreateAdmin_DuplicateExists_ReturnsAdminWithMinusOneId() {
        Admin input = admin(null, "john_admin", "pw", "9876543210", "john@gym.com", false);
        when(repository.findByUsernameOrMobileOrEmail("john_admin", "9876543210", "john@gym.com"))
                .thenReturn(Optional.of(admin("7", "john_admin", "hash", null, null, true)));

        Admin result = adminCrudService.createAdmin(input);

        assertEquals("-1", result.adminId());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should assign ID '0' to the primary admin without touching the sequence")
    void testCreateAdmin_PrimaryAdmin_AssignsIdZero() {
        when(repository.findByUsernameOrMobileOrEmail(any(), any(), any())).thenReturn(Optional.empty());
        when(repository.save(any(Admin.class))).thenAnswer(i -> i.getArgument(0));

        Admin result = adminCrudService.createAdmin(admin(null, "admin", "pw", null, null, false));

        assertEquals("0", result.adminId());
        verify(idGenerationService, never()).nextAdminId();
    }

    @Test
    @DisplayName("Should take the next sequence value and store a hashed password for a standard admin")
    void testCreateAdmin_StandardAdmin_GeneratesNewIdAndHashesPassword() {
        when(repository.findByUsernameOrMobileOrEmail(any(), any(), any())).thenReturn(Optional.empty());
        when(idGenerationService.nextAdminId()).thenReturn("101");
        when(repository.save(any(Admin.class))).thenAnswer(i -> i.getArgument(0));

        Admin result = adminCrudService.createAdmin(admin(null, "branch_admin", "pw", null, null, false));

        assertEquals("101", result.adminId());
        assertEquals("101", result.branchId());
        assertTrue(passwordService.matches("pw", result.password()));
    }

    @Test
    @DisplayName("Should authenticate against the stored hash")
    void testAuthenticate_ValidPassword_ReturnsAdmin() {
        Admin stored = admin("101", "branch_admin", passwordService.hash("pw"), null, null, true);
        when(repository.findCredByUsername("branch_admin")).thenReturn(Optional.of(stored));

        assertEquals("101", adminCrudService.authenticate("branch_admin", "pw").adminId());
        assertNull(adminCrudService.authenticate("branch_admin", "wrong").adminId());
    }

    @Test
    @DisplayName("Should upgrade a legacy plain-text password to BCrypt on successful login")
    void testAuthenticate_LegacyPassword_IsUpgraded() {
        when(repository.findCredByUsername("old_admin")).thenReturn(Optional.of(admin("5", "old_admin", "pw", null, null, true)));
        when(repository.save(any(Admin.class))).thenAnswer(i -> i.getArgument(0));

        adminCrudService.authenticate("old_admin", "pw");

        ArgumentCaptor<Admin> saved = ArgumentCaptor.forClass(Admin.class);
        verify(repository).save(saved.capture());
        assertTrue(saved.getValue().password().startsWith("$2"));
    }

    @Test
    @DisplayName("Should return inactive admins wrapper")
    void testFindInactiveAdmins_ReturnsWrapper() {
        when(repository.findInactiveAdmins()).thenReturn(List.of(admin("101", "a", null, null, null, false)));

        AdminWrapper result = adminCrudService.findInactiveAdmins();

        assertEquals(1, result.allAdmins().size());
    }

    @Test
    @DisplayName("Should save the admin as active on updateAdminStatus")
    void testUpdateAdminStatus_Success() {
        when(repository.findByAdminId("101")).thenReturn(Optional.of(admin("101", "a", null, null, null, false)));

        assertTrue(adminCrudService.updateAdminStatus("101"));

        ArgumentCaptor<Admin> saved = ArgumentCaptor.forClass(Admin.class);
        verify(repository).save(saved.capture());
        assertTrue(saved.getValue().isActive());
    }
}
