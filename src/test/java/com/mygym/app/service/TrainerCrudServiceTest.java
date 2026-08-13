package com.mygym.app.service;

import com.mygym.app.models.Trainer;
import com.mygym.app.repository.TrainerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerCrudServiceTest {

    @Mock
    private TrainerRepository repository;

    @Mock
    private IdGenerationService idGenerationService;

    @InjectMocks
    private TrainerCrudService trainerCrudService;

    @Test
    @DisplayName("Should create trainer profile and increment trainer ID sequence")
    void testCreateTrainer_Success() {
        Trainer trainer = new Trainer();
        trainer.setUsername("trainer_mike");

        when(repository.findByUsername("trainer_mike")).thenReturn(Optional.empty());
        when(idGenerationService.getTrainerId()).thenReturn(201L);
        when(repository.save(any(Trainer.class))).thenAnswer(i -> i.getArgument(0));

        Trainer created = trainerCrudService.createTrainer(trainer);

        assertEquals("201", created.getTrainerId());
        verify(idGenerationService, times(1)).incrementTrainerId();
    }

    @Test
    @DisplayName("Should approve trainer profile by setting isActive to true")
    void testApproveTrainer_Success() {
        Trainer trainer = new Trainer();
        trainer.setTrainerId("201");
        trainer.setIsActive(false);

        when(repository.findByTrainerId("201")).thenReturn(Optional.of(trainer));

        Boolean approved = trainerCrudService.approveTrainer("201");

        assertTrue(approved);
        assertTrue(trainer.getIsActive());
        verify(repository, times(1)).save(trainer);
    }

    @Test
    @DisplayName("Should disable trainer profile by setting isActive to false")
    void testDisableTrainer_Success() {
        Trainer trainer = new Trainer();
        trainer.setTrainerId("201");
        trainer.setIsActive(true);

        when(repository.findByTrainerId("201")).thenReturn(Optional.of(trainer));

        Boolean disabled = trainerCrudService.disableTrainer("201");

        assertTrue(disabled);
        assertFalse(trainer.getIsActive());
        verify(repository, times(1)).save(trainer);
    }
}