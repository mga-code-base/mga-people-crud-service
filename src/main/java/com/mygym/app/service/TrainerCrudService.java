package com.mygym.app.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.mygym.app.models.Trainer;
import com.mygym.app.models.TrainerWrapper;
import com.mygym.app.repository.TrainerRepository;

/**
 * Service managing gym trainer profiles, credentials, and active account status updates.
 */
@Service
public class TrainerCrudService {

    private final TrainerRepository repository;
    private final IdGenerationService service;
    
    public TrainerCrudService(TrainerRepository repository, IdGenerationService service) {
        this.service = service;
        this.repository = repository;
    }

    public Trainer createTrainer(Trainer trainer) {
        if (repository.findByUsername(trainer.getUsername()).isPresent()) {
            return new Trainer("-1");
        }
        trainer.setTrainerId(String.valueOf(service.getTrainerId()));
        service.incrementTrainerId();
        return repository.save(trainer);
    }

    public List<Trainer> getAllTrainers() {
        return repository.findAll();
    }

    public Trainer getTrainerById(String id) {
        return repository.findByTrainerId(id)
                .orElseThrow(() -> new RuntimeException("Trainer profile not found with id: " + id));
    }

    public Trainer updateTrainer(String id, Trainer updatedData) {
        Trainer existing = getTrainerById(id);
        existing.setName(updatedData.getName());
        existing.setUsername(updatedData.getUsername());
        existing.setPassword(updatedData.getPassword());
        existing.setMobileNumber(updatedData.getMobileNumber());
        existing.setEmailAddress(updatedData.getEmailAddress());
        existing.setIsActive(updatedData.getIsActive());
        existing.setActiveClients(updatedData.getActiveClients());
        return repository.save(existing);
    }

    public Boolean deleteTrainer(String id) {
        try {
            repository.deleteByTrainerId(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public Trainer findCredByUsername(String username) {
        return repository.findCredByUsername(username).map(trainer -> trainer).orElse(new Trainer("-1"));
    }
    
    public TrainerWrapper findInactiveTrainers() {
        List<Trainer> trainers = repository.findInactiveTrainers();
        return (Objects.isNull(trainers) || trainers.isEmpty()) 
                ? new TrainerWrapper() 
                : new TrainerWrapper(trainers);
    }

    public Trainer readByUsername(String username) {
        return repository.findByUsername(username).map(trainer -> trainer).orElse(new Trainer("-1"));
    }

    public Boolean approveTrainer(String trainerId) {
        repository.findByTrainerId(trainerId).ifPresent(trainer -> {
            trainer.setIsActive(true);
            repository.save(trainer);
        });
        return true;
    }

    public Boolean disableTrainer(String trainerId) {
        repository.findByTrainerId(trainerId).ifPresent(trainer -> {
            trainer.setIsActive(false);
            repository.save(trainer);
        });
        return true;
    }
}