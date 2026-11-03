package com.mygym.app.service;

import static com.mygym.app.service.PeopleFixtures.trainer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mygym.app.models.Trainer;
import com.mygym.app.repository.TrainerRepository;

@ExtendWith(MockitoExtension.class)
class TrainerCrudServiceTest {

    @Mock
    private TrainerRepository repository;

    @Mock
    private IdGenerationService idGenerationService;

    @Spy
    private PasswordService passwordService = new PasswordService();

    @InjectMocks
    private TrainerCrudService trainerCrudService;

    @Test
    @DisplayName("Should create trainer with the next sequence ID and a hashed password")
    void testCreateTrainer_Success() {
        when(repository.findByUsername("trainer_mike")).thenReturn(Optional.empty());
        when(idGenerationService.nextTrainerId()).thenReturn("201");
        when(repository.save(any(Trainer.class))).thenAnswer(i -> i.getArgument(0));

        Trainer created = trainerCrudService.createTrainer(trainer(null, "trainer_mike", "pw", false));

        assertEquals("201", created.trainerId());
        assertTrue(passwordService.matches("pw", created.password()));
    }

    @Test
    @DisplayName("Should approve trainer profile by saving isActive true")
    void testApproveTrainer_Success() {
        when(repository.findByTrainerId("201")).thenReturn(Optional.of(trainer("201", "t", null, false)));

        assertTrue(trainerCrudService.approveTrainer("201"));

        ArgumentCaptor<Trainer> saved = ArgumentCaptor.forClass(Trainer.class);
        verify(repository).save(saved.capture());
        assertTrue(saved.getValue().isActive());
    }

    @Test
    @DisplayName("Should disable trainer profile by saving isActive false")
    void testDisableTrainer_Success() {
        when(repository.findByTrainerId("201")).thenReturn(Optional.of(trainer("201", "t", null, true)));

        assertTrue(trainerCrudService.disableTrainer("201"));

        ArgumentCaptor<Trainer> saved = ArgumentCaptor.forClass(Trainer.class);
        verify(repository).save(saved.capture());
        assertFalse(saved.getValue().isActive());
    }
}
