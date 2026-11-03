package com.mygym.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mygym.app.models.Trainer;
import com.mygym.app.models.TrainerWrapper;
import com.mygym.app.repository.TrainerRepository;

@Service
public class TrainerCrudService {

    private final TrainerRepository repository;
    private final IdGenerationService idGenerationService;
    private final PasswordService passwordService;

    public TrainerCrudService(TrainerRepository repository, IdGenerationService idGenerationService, PasswordService passwordService) {
        this.repository = repository;
        this.idGenerationService = idGenerationService;
        this.passwordService = passwordService;
    }

    public Trainer createTrainer(Trainer trainer) {
        if (repository.findByUsername(trainer.username()).isPresent()) {
            return Trainer.notFound();
        }
        return repository.save(trainer
                .withTrainerId(idGenerationService.nextTrainerId())
                .withPassword(passwordService.hash(trainer.password())));
    }

    public List<Trainer> getAllTrainers() {
        return repository.findAll();
    }

    public Trainer getTrainerById(String id) {
        return repository.findByTrainerId(id).orElse(Trainer.notFound());
    }

    public Trainer updateTrainer(String id, Trainer updatedData) {
        return repository.findByTrainerId(id)
                .map(existing -> repository.save(existing.withProfileFrom(updatedData,
                        passwordService.hashOrKeep(updatedData.password(), existing.password()))))
                .orElse(Trainer.notFound());
    }

    public Boolean deleteTrainer(String id) {
        repository.deleteByTrainerId(id);
        return true;
    }

    public Trainer authenticate(String username, String password) {
        return repository.findCredByUsername(username)
                .filter(trainer -> passwordService.matches(password, trainer.password()))
                .map(trainer -> passwordService.needsUpgrade(trainer.password())
                        ? repository.save(trainer.withPassword(passwordService.hash(password)))
                        : trainer)
                .orElse(Trainer.notFound());
    }

    public TrainerWrapper findInactiveTrainers() {
        return new TrainerWrapper(repository.findInactiveTrainers());
    }

    public Trainer readByUsername(String username) {
        return repository.findByUsername(username).orElse(Trainer.notFound());
    }

    public Boolean approveTrainer(String trainerId) {
        return setActive(trainerId, true);
    }

    public Boolean disableTrainer(String trainerId) {
        return setActive(trainerId, false);
    }

    private Boolean setActive(String trainerId, boolean active) {
        repository.findByTrainerId(trainerId).ifPresent(trainer -> repository.save(trainer.withIsActive(active)));
        return true;
    }
}
