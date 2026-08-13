package com.mygym.app.controller;

import static com.mygym.app.util.PeopleCrudConstants.ALL_INACTIVE_TRAINERS_ENDPOINT;
import static com.mygym.app.util.PeopleCrudConstants.ALL_TRAINERS_ENDPOINT;
import static com.mygym.app.util.PeopleCrudConstants.APPROVE_BY_TRAINER_ID_ENDPOINT;
import static com.mygym.app.util.PeopleCrudConstants.DISABLE_BY_TRAINER_ID_ENDPOINT;
import static com.mygym.app.util.PeopleCrudConstants.ID_PLACEHOLDER_ENDPOINT;
import static com.mygym.app.util.PeopleCrudConstants.TRAINER_CRUD_REST_BASE;
import static com.mygym.app.util.PeopleCrudConstants.TRAINER_ID_PLACEHOLDER_ENDPOINT;
import static com.mygym.app.util.PeopleCrudConstants.USERNAME_PLACEHOLDER_ENDPOINT;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mygym.app.models.Trainer;
import com.mygym.app.models.TrainerWrapper;
import com.mygym.app.service.TrainerCrudService;

@RestController
@RequestMapping(TRAINER_CRUD_REST_BASE)
public class TrainerCrudController {

	private static final Logger logger = LoggerFactory.getLogger(TrainerCrudController.class);
    private final TrainerCrudService service;

    public TrainerCrudController(TrainerCrudService service) {
        this.service = service;
    }

    @PostMapping
    public String create(@RequestBody Trainer trainer) {
        return service.createTrainer(trainer).getTrainerId();
    }

    @GetMapping(ALL_TRAINERS_ENDPOINT)
    public List<Trainer> readAll() {
        return service.getAllTrainers();
    }

    @GetMapping(ID_PLACEHOLDER_ENDPOINT)
    public Trainer readById(@PathVariable("id") String id) {
        return service.getTrainerById(id);
    }
    
    @GetMapping(USERNAME_PLACEHOLDER_ENDPOINT)
    public Trainer readByUsername(@PathVariable("username") String username) {
        return service.readByUsername(username);
    }
    
    @GetMapping
    public Trainer findCredByUsername(@RequestParam("username") String username) {
        logger.info("Login request received on Trainer CRUD Controller tracking user execution");
    	return service.findCredByUsername(username);
    }
    
    @PutMapping(ID_PLACEHOLDER_ENDPOINT)
    public Trainer update(@PathVariable String id, @RequestBody Trainer trainer) {
        return service.updateTrainer(id, trainer);
    }
    
    @GetMapping(ALL_INACTIVE_TRAINERS_ENDPOINT)
    public TrainerWrapper findInactiveTrainers(){
    	return service.findInactiveTrainers();
    }

    @PutMapping(APPROVE_BY_TRAINER_ID_ENDPOINT)
    public Boolean approveTrainer(@PathVariable("trainerid") String trainerId) {
    	return service.approveTrainer(trainerId);
    }
    
    @PutMapping(DISABLE_BY_TRAINER_ID_ENDPOINT)
    public Boolean disableTrainer(@PathVariable("trainerid") String trainerId) {
    	return service.disableTrainer(trainerId);
    }
    
    @DeleteMapping(TRAINER_ID_PLACEHOLDER_ENDPOINT)
    public Boolean deleteTrainer(@PathVariable("trainerid") String trainerId) {
    	if(service.deleteTrainer(trainerId))
    		return true;
    	return false;
    }
}
