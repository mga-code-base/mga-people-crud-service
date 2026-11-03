package com.mygym.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PasswordServiceTest {

    private final PasswordService passwordService = new PasswordService();

    @Test
    @DisplayName("Should hash plain passwords with BCrypt and verify them")
    void testHashAndMatch() {
        String hash = passwordService.hash("secret");

        assertNotEquals("secret", hash);
        assertTrue(hash.startsWith("$2"));
        assertTrue(passwordService.matches("secret", hash));
        assertFalse(passwordService.matches("wrong", hash));
    }

    @Test
    @DisplayName("Should not re-hash a value that is already a BCrypt hash")
    void testHashIsIdempotent() {
        String hash = passwordService.hash("secret");

        assertEquals(hash, passwordService.hash(hash));
    }

    @Test
    @DisplayName("Should accept legacy plain-text passwords and flag them for upgrade")
    void testLegacyPlainTextPassword() {
        assertTrue(passwordService.matches("secret", "secret"));
        assertTrue(passwordService.needsUpgrade("secret"));
        assertFalse(passwordService.needsUpgrade(passwordService.hash("secret")));
    }

    @Test
    @DisplayName("Should keep the current hash when no new password is supplied")
    void testHashOrKeep() {
        String current = passwordService.hash("secret");

        assertEquals(current, passwordService.hashOrKeep(null, current));
        assertEquals(current, passwordService.hashOrKeep(" ", current));
        assertTrue(passwordService.matches("changed", passwordService.hashOrKeep("changed", current)));
    }

    @Test
    @DisplayName("Should reject blank credentials")
    void testBlankCredentials() {
        assertNull(passwordService.hash(""));
        assertFalse(passwordService.matches("", "secret"));
        assertFalse(passwordService.matches("secret", null));
    }
}
