package com.mygym.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.mygym.app.models.Trainer;

@Repository
public interface TrainerRepository extends MongoRepository<Trainer, String> {

	Optional<Trainer> findByTrainerId(String trainerId);
	
	void deleteByTrainerId(String trainerId);

    @Query(value = "{ 'username' : ?0 }")
	Optional<Trainer> findByUsername(String username);
	
    @Query(value = "{ 'username' : ?0 }")
    Optional<Trainer> findCredByUsername(String username);
    
    @Query(value = "{ 'trainerId': { '$ne': 0 }, '$or': [ { 'isActive': null }, { 'isActive': false } ] }", 
            fields = "{ 'password': 0 }")
    List<Trainer> findInactiveTrainers();
}
