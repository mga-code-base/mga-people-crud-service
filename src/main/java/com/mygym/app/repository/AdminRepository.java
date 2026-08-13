package com.mygym.app.repository;

import com.mygym.app.models.Admin;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends MongoRepository<Admin, String> {

    // Custom query builder to find documents matching your custom admin_id field
    Optional<Admin> findByAdminId(String adminId);
    
    Optional<Admin> findByUsername(String username);
    
    @Query("{ '$or': [ { 'username': ?0 }, { 'mobileNumber': ?1 }, { 'emailAddress': ?2 } ] }")
    Optional<Admin> findByUsernameOrMobileOrEmail(String username, String mobileNumber, String emailAddress);
    
    @Query(value = "{ 'username' : ?0 }")
    Optional<Admin> findCredByUsername(String username);
    
    @Query(value = "{ 'adminId': { '$ne': 0 }, '$or': [ { 'isActive': null }, { 'isActive': false } ] }", 
            fields = "{ 'password': 0 }")
    List<Admin> findInactiveAdmins();
    
    Optional<Admin> deleteByAdminId(String adminId);
}
