package com.mygym.app.service;

import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdGenerationServiceTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private IdGenerationService idGenerationService;

    @Test
    @DisplayName("Should pass init when sequence document exists in MongoDB")
    void testInit_SequenceDocumentExists_Success() {
        when(mongoTemplate.exists(any(Query.class), eq("_people"))).thenReturn(true);

        assertDoesNotThrow(() -> idGenerationService.init());
    }

    @Test
    @DisplayName("Should throw RuntimeException during init when sequence document is missing")
    void testInit_SequenceDocumentMissing_ThrowsException() {
        when(mongoTemplate.exists(any(Query.class), eq("_people"))).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> idGenerationService.init());
        assertTrue(exception.getMessage().contains("Failed to verify ID sequences in MongoDB"));
    }

    @Test
    @DisplayName("Should fetch current admin id value")
    void testGetAdminId_Success() {
        Document doc = new Document("nextAdminId", 105L);
        when(mongoTemplate.findOne(any(Query.class), eq(Document.class), eq("_people"))).thenReturn(doc);

        Long adminId = idGenerationService.getAdminId();

        assertEquals(105L, adminId);
    }

    @Test
    @DisplayName("Should increment admin id successfully")
    void testIncrementAdminId_Success() {
        Document updatedDoc = new Document("nextAdminId", 106L);
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(Document.class), eq("_people")))
                .thenReturn(updatedDoc);

        assertDoesNotThrow(() -> idGenerationService.incrementAdminId());
        verify(mongoTemplate, times(1)).findAndModify(any(), any(), any(), eq(Document.class), eq("_people"));
    }
}