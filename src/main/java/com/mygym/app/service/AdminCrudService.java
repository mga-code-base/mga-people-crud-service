package com.mygym.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mygym.app.models.Admin;
import com.mygym.app.models.AdminWrapper;
import com.mygym.app.repository.AdminRepository;

@Service
public class AdminCrudService {

    private static final String PRIMARY_ADMIN_USERNAME = "admin";
    private static final String PRIMARY_ADMIN_ID = "0";

    private final AdminRepository repository;
    private final IdGenerationService idGenerationService;
    private final PasswordService passwordService;

    public AdminCrudService(AdminRepository repository, IdGenerationService idGenerationService, PasswordService passwordService) {
        this.repository = repository;
        this.idGenerationService = idGenerationService;
        this.passwordService = passwordService;
    }

    public Admin createAdmin(Admin admin) {
        Optional<Admin> existing = repository.findByUsernameOrMobileOrEmail(admin.username(), admin.mobileNumber(), admin.emailAddress());
        if (existing.isPresent()) {
            return existing.get().withIds("-1", existing.get().branchId());
        }
        Admin withIds = PRIMARY_ADMIN_USERNAME.equals(admin.username())
                ? admin.withIds(PRIMARY_ADMIN_ID, admin.branchId())
                : assignNewIds(admin);
        return repository.save(withIds.withPassword(passwordService.hash(admin.password())));
    }

    private Admin assignNewIds(Admin admin) {
        String adminId = idGenerationService.nextAdminId();
        return admin.withIds(adminId, adminId);
    }

    public List<Admin> getAllAdmins() {
        return repository.findAll();
    }

    public Optional<Admin> findByAdminId(String id) {
        return repository.findByAdminId(id);
    }

    public Admin getAdminByAdminId(String id) {
        return findByAdminId(id).orElse(Admin.notFound());
    }

    public Admin updateAdmin(String id, Admin updatedData) {
        return findByAdminId(id)
                .map(existing -> repository.save(existing.withProfileFrom(updatedData,
                        passwordService.hashOrKeep(updatedData.password(), existing.password()))))
                .orElse(Admin.notFound());
    }

    public Boolean createPrimaryAdmin(String password) {
        Admin created = createAdmin(Admin.primary(PRIMARY_ADMIN_USERNAME, password));
        return Long.parseLong(created.adminId()) >= 0;
    }

    public Admin authenticate(String username, String password) {
        return repository.findCredByUsername(username)
                .filter(admin -> passwordService.matches(password, admin.password()))
                .map(admin -> passwordService.needsUpgrade(admin.password())
                        ? repository.save(admin.withPassword(passwordService.hash(password)))
                        : admin)
                .orElse(Admin.empty());
    }

    public AdminWrapper findInactiveAdmins() {
        return new AdminWrapper(repository.findInactiveAdmins());
    }

    public Boolean updateAdminStatus(String adminId) {
        repository.findByAdminId(adminId).ifPresent(admin -> repository.save(admin.withIsActive(true)));
        return true;
    }

    public Boolean deleteByAdminId(String adminId) {
        repository.deleteByAdminId(adminId);
        return true;
    }
}
