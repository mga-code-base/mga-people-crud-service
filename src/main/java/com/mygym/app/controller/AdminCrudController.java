package com.mygym.app.controller;

import static com.mygym.app.util.PeopleCrudConstants.ADMIN_CRUD_REST_BASE;
import static com.mygym.app.util.PeopleCrudConstants.CREATE_PRIMARY_ENDPOINT;
import static com.mygym.app.util.PeopleCrudConstants.ID_PLACEHOLDER_ENDPOINT;
import static com.mygym.app.util.PeopleCrudConstants.INACTIVE_ADMIN_ENDPOINT;
import static com.mygym.app.util.PeopleCrudConstants.UPDATE_ADMIN_STATUS_ENDPOINT;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mygym.app.models.Admin;
import com.mygym.app.models.AdminWrapper;
import com.mygym.app.service.AdminCrudService;

@RestController
@RequestMapping(ADMIN_CRUD_REST_BASE)
public class AdminCrudController {

    private final AdminCrudService service;

    public AdminCrudController(AdminCrudService service) {
        this.service = service;
    }

    @PostMapping
    public Admin create(@RequestBody Admin admin) {
        return service.createAdmin(admin);
    }
    
    @PostMapping(CREATE_PRIMARY_ENDPOINT)
    public Boolean createPrimaryAdmin(@RequestParam("password") String password) {
        return service.createPrimaryAdmin(password);
    }

    @GetMapping(ID_PLACEHOLDER_ENDPOINT)
    public Admin readByAdminId(@PathVariable(name = "id") String id) {
        try {
			return service.getAdminByAdminId(id);
		} catch (RuntimeException e) {
			return new Admin("-1");
		}
    }

    @PutMapping(ID_PLACEHOLDER_ENDPOINT)
    public Admin update(@PathVariable String id, @RequestBody Admin admin) {
        return service.updateAdmin(id, admin);
    }
    
    @GetMapping
    public Admin findCredByUsername(@RequestParam("username") String username) {
    	return service.findCredByUsername(username);
    }
    
    @GetMapping(INACTIVE_ADMIN_ENDPOINT)
    public AdminWrapper findInactiveAdmins(){
    	return service.findInactiveAdmins();
    }
    
    @PutMapping(UPDATE_ADMIN_STATUS_ENDPOINT)
    public Boolean updateAdminStatus(@PathVariable(name = "id") String adminId) {
    	return service.updateAdminStatus(adminId);
    }
    
    @DeleteMapping(ID_PLACEHOLDER_ENDPOINT)
    public Boolean deleteByAdminId(@PathVariable(name = "id") String adminId) {
    	return service.deleteByAdminId(adminId);
    }
}
