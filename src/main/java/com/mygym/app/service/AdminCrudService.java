package com.mygym.app.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mygym.app.models.Admin;
import com.mygym.app.models.AdminWrapper;
import com.mygym.app.repository.AdminRepository;

/**
 * Service managing administrative account registration, ID assignment, and status updates.
 */
@Service
public class AdminCrudService {
	
    private String PRIMARY_ADMIN_USERNAME = "admin";
    private final AdminRepository repository;
    private final IdGenerationService service;

    public AdminCrudService(AdminRepository repository, IdGenerationService service) {
        this.service = service;
        this.repository = repository;
    }

    /**
     * Creates a new Admin account or returns existing record with ID '-1' if a duplicate exists.
     */
    public Admin createAdmin(Admin admin) {
        Optional<Admin> existingAdminOptional = repository.findByUsernameOrMobileOrEmail(admin.getUsername(), 
                admin.getMobileNumber(),
                admin.getEmailAddress());
        if (existingAdminOptional.isPresent()) {
            Admin existingAdmin = existingAdminOptional.get();
            existingAdmin.setAdminId("-1");
            return existingAdmin;
        }
        if (admin.getUsername().contentEquals(PRIMARY_ADMIN_USERNAME)) {
            admin.setAdminId("0");
        } else {
            admin.setAdminId(String.valueOf(service.getAdminId()));
            admin.setBranch(admin.getAdminId());
            service.incrementAdminId();
        }
        return repository.save(admin);
    }

    public List<Admin> getAllAdmins() {
        return repository.findAll();
    }

    public Admin getAdminByAdminId(String id) {
        return repository.findByAdminId(id)
                .orElseThrow(() -> new RuntimeException("Admin profile not found with id: " + id));
    }

    public Admin updateAdmin(String id, Admin updatedData) {
        Admin existing = getAdminByAdminId(id);
        existing.setName(updatedData.getName());
        existing.setUsername(updatedData.getUsername());
        existing.setPassword(updatedData.getPassword());
        existing.setMobileNumber(updatedData.getMobileNumber());
        existing.setEmailAddress(updatedData.getEmailAddress());
        existing.setIsActive(updatedData.getIsActive());
        
        return repository.save(existing);
    }
    
    public Boolean createPrimaryAdmin(String password) {
        Admin newAdmin = createAdmin(new Admin("0", PRIMARY_ADMIN_USERNAME, password, "-1", "-1"));
        return Long.valueOf(newAdmin.getAdminId()) >= 0;
    }
    
    public Admin findCredByUsername(String username) {
        Optional<Admin> optionalAdmin = repository.findCredByUsername(username);
        if (optionalAdmin.isPresent() && Objects.nonNull(optionalAdmin.get())) 
            return optionalAdmin.get();
        return new Admin();
    }
    
    public AdminWrapper findInactiveAdmins() {
        List<Admin> optionalAdmin = repository.findInactiveAdmins();
        if (Objects.nonNull(optionalAdmin) && !optionalAdmin.isEmpty()) 
            return new AdminWrapper(optionalAdmin);
        return null;
    }
    
    public Boolean updateAdminStatus(String adminId) {
        repository.findByAdminId(adminId).ifPresent(admin -> {
            admin.setIsActive(true);
            repository.save(admin);
        });
        return true;
    }
    
    public Boolean deleteByAdminId(String adminId) {
        repository.findByAdminId(adminId).ifPresent(admin -> {
            repository.deleteByAdminId(adminId);
        });
        return true;
    }
}